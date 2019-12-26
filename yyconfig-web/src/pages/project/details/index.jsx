import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Card, Button, Descriptions, Icon, Row, Col, Menu } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import CreateProject from '../create/';
import styles from '../index.less';
import RightContent from './rightContent/';
import ClusterAdd from './modal/ClusterAdd';
import NamespaceAdd from './modal/NamespaceAdd';
import router from 'umi/router';

const { SubMenu } = Menu;

class ProjectDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showProjectEdit: false,
      appId: '',
      appCode: '',
      showClusterModal: false,
      showNamespace: false
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() {
    this.onGetBaseInfo()
  }
  componentDidUpdate(prevProps, prevState) {
    const { envList, dispatch, currentEnv, location } = this.props;
    if (prevProps.envList !== envList) {
      let envFirst = envList[0] || {};
      this.onEnvClick(envFirst)
    }
    if (prevProps.currentEnv !== currentEnv) {
      this.onFetchNamespaceList();
    }
    if(prevProps.location.query.appId !== location.query.appId) {
      this.onGetBaseInfo()
    }
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/clearData',
      payload: {
        envList: [],
        currentEnv: {},
        nameSpaceList: []
      }
    })
  }

  //------------------------事件------------------------------------
  onGetBaseInfo=()=>{
    const { location } = this.props;
    let query = location.query ? location.query : {};
    this.setState({
      appId: query.appId,
      appCode: query.appCode
    }, () => {
      this.onFetchAppItem()
      this.onFetchEnvList();
    })
  }
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
    const { appCode } = this.state;
    let currentCluster = currentEnv.cluster || {};
    dispatch({
      type: 'project/nameSpaceList',
      payload: {
        appCode,
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
        cluster: item.clusters && item.clusters.length ? item.clusters[0] : {}
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
  //添加命名空间成功
  onNamespaceAddSave = () => {
    this.onFetchNamespaceList();
  }
  //添加集群成功
  onClusterAddSave=()=>{
    this.onFetchEnvList();
  }
  //------------------------渲染------------------------------------
  renderBaseInfo() {
    const { appDetail } = this.props;
    let department = appDetail.department || {}, appOwner = appDetail.appOwner || {};
    return (
      <Descriptions size="small" column={5}>
        <Descriptions.Item label="项目Code">{appDetail.appCode}</Descriptions.Item>
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
        <SubMenu title={item.env} key={`${item.env}${index}`}>
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
        {
          currentEnv.env &&
          <Menu mode="inline" style={{ width: '100%' }} selectedKeys={[curentKey]} defaultOpenKeys={[currentEnv['env'] + '0']}>
            {
              envList && envList.map((item, i) => this.renderSubMenuOrItem(item, i))
            }
          </Menu>
        }
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
          showClusterModal && <ClusterAdd onCancel={() => this.onCancel('showClusterModal')} onSave={this.onClusterAddSave}/>
        }
        {
          showNamespace && <NamespaceAdd onCancel={() => this.onCancel('showNamespace')} onSave={this.onNamespaceAddSave} />
        }
      </Fragment>
    )
  }

  render() {
    const { showProjectEdit, appId } = this.state;

    return (
      <PageHeaderWrapper title="项目信息" content={this.renderBaseInfo()} extra={this.renderEdit()}>
        <Row type="flex" gutter={24}>
          <Col span={4}>
            {this.renderEnv()}
            {this.renderOpe()}
          </Col>
          <Col span={20}>
            <RightContent />
          </Col>
        </Row>
        {/* <div className={styles.mainBox}>
          <div className={styles.leftBox}>
            {this.renderEnv()}
            {this.renderOpe()}
          </div>
          <div className={styles.rightBox}>
            <RightContent />
          </div>
        </div> */}
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
