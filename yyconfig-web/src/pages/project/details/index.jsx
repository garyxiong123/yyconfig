import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Card, Button, Descriptions, Icon, Row, Col, Menu } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import CreateProject from '../create/';
import styles from '../index.less';
import RightContent from './rightContent/';
import ClusterAdd from './modal/ClusterAdd';
import NamespaceAdd from './modal/NamespaceAdd';

const { SubMenu } = Menu;

class ProjectDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showProjectEdit: false,
      appId: '',
      showClusterModal: false,
      showNamespace: false
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() {
    const { location } = this.props;
    let appId = location.query ? location.query.appId : '';
    this.setState({
      appId: appId
    }, () => {
      this.onFetchAppItem()
      this.onFetchEnvList();
    })
  }
  componentDidUpdate(prevProps, prevState) {
    const { envList, dispatch, currentEnv } = this.props;
    if (prevProps.envList !== envList) {
      let envFirst = envList[0];
      this.onEnvClick(envFirst)
    }
    if (prevProps.currentEnv !== currentEnv) {
      this.onFetchNamespaceList();
    }
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/clearData',
      payload: {
        envList: [],
        currentEnv: {}
      }
    })
  }

  //------------------------事件------------------------------------
  onFetchAppItem = () => {
    const { dispatch } = this.props;
    const { appId } = this.state;
    if (!appId) {
      return
    }
    dispatch({
      type: 'project/appDetail',
      payload: { appId }
    })
  }
  onFetchEnvList = () => {
    const { dispatch } = this.props;
    const { appId } = this.state;
    dispatch({
      type: 'project/envList',
      payload: { appId }
    })
  }
  onSetCurrentEnv = (currentEnv) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/setCurrentEnv',
      payload: currentEnv
    })
  }
  onFetchNamespaceList = () => {
    const { dispatch, appDetail, currentEnv } = this.props;
    let currentCluster = currentEnv.cluster || {};
    dispatch({
      type: 'project/nameSpaceList',
      payload: {
        appCode: appDetail.appCode,
        env: currentEnv.env,
        clusterName: currentCluster.name
      }
    })
  }

  onSave = () => {
    this.onFetchAppItem();
  }
  onEnvClick = (item, vo) => {
    if (vo) {
      this.onSetCurrentEnv({
        env: item.env,
        cluster: vo
      })
    } else {
      this.onSetCurrentEnv({
        env: item.env,
        cluster: item.clusters[0]
      })
    }

  }

  onShowModal = (type) => {
    this.setState({
      [type]: true
    })
  }
  onCancel = (type) => {
    this.setState({
      [type]: false
    })
  }
  //------------------------渲染------------------------------------
  renderBaseInfo() {
    const { appDetail } = this.props;
    let department = appDetail.department || {}, appOwner = appDetail.appOwner || {};
    return (
      <Descriptions size="small" column={3}>
        <Descriptions.Item label="项目Id">{appDetail.appCode}</Descriptions.Item>
        <Descriptions.Item label="项目名">{appDetail.name}</Descriptions.Item>
        <Descriptions.Item label="部门">{department.name}</Descriptions.Item>
        <Descriptions.Item label="负责人">{appOwner.realName}</Descriptions.Item>
        <Descriptions.Item label="邮箱">{appOwner.email}</Descriptions.Item>
      </Descriptions>
    )
  }
  renderEdit() {
    return (
      <a onClick={() => this.onShowModal('showProjectEdit')}>
        <Icon type="edit" theme="twoTone" style={{ fontSize: 20 }} title="编辑项目" />
      </a>
    )
  }
  renderSubMenuOrItem(item, index) {
    let clusters = item.clusters || [];
    if (clusters.length > 1) {
      return (
        <SubMenu title={item.env} key={index}>
          {clusters.map((vo, i) => (
            <Menu.Item key={vo.id} onClick={() => this.onEnvClick(item, vo)}>{vo.name}</Menu.Item>
          ))}
        </SubMenu>
      )
    } else {
      return (
        <Menu.Item key={clusters[0] && clusters[0].id} onClick={() => this.onEnvClick(item)}>{item.env}</Menu.Item>
      )
    }
  }
  renderEnv() {
    const { envList, currentEnv } = this.props;
    let cluster = currentEnv.cluster || {};
    let curentKey = cluster.id && cluster.id.toString();
    return (
      <Card title="环境列表">
        <Menu mode="inline" style={{ width: '100%' }} selectedKeys={[curentKey]}>
          {
            envList && envList.map((item, i) => this.renderSubMenuOrItem(item, i))
          }
        </Menu>
      </Card>
    )
  }
  renderOpe() {
    const { showClusterModal, showNamespace } = this.state;
    return (
      <Fragment>
        <Card className={styles.marginTop20} title="操作">
          <Button block type="dashed" onClick={() => this.onShowModal('showClusterModal')}>+ 添加集群</Button>
          <Button block className={styles.marginTop20} type="dashed" onClick={() => this.onShowModal('showNamespace')}>+ 添加命名空间</Button>
          <Button block className={styles.marginTop20} type="dashed">命名空间管理</Button>
        </Card>
        {
          showClusterModal && <ClusterAdd onCancel={() => this.onCancel('showClusterModal')} />
        }
        {
          showNamespace && <NamespaceAdd onCancel={() => this.onCancel('showNamespace')} />
        }
      </Fragment>
    )
  }

  render() {
    const { showProjectEdit, appId } = this.state;
    return (
      <PageHeaderWrapper title="项目信息" content={this.renderBaseInfo()} extra={this.renderEdit()}>
        <Row type="flex" gutter={24}>
          <Col span={6}>
            {this.renderEnv()}
            {this.renderOpe()}
          </Col>
          <Col span={18}>
            <RightContent />
          </Col>
        </Row>
        {
          showProjectEdit && <CreateProject onCancel={() => this.onCancel('showProjectEdit')} onSave={this.onSave} appId={appId} />
        }
      </PageHeaderWrapper>
    );
  }
}

export default connect(({ project, loading }) => ({
  appDetail: project.appDetail,
  envList: project.envList,
  currentEnv: project.currentEnv
  // loading: loading.effects["project/appList"]
}))(ProjectDetail);
