import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Button, Icon, Collapse, Tabs, Table, Spin, Tag, Row, Col, Dropdown, Menu, Empty, message } from 'antd';
import { Loading } from '@/pages/components/';
import styles from '../../index.less';
import TextContent from './TextContent';
import TableList from './TableList';
import History from './History';
import Case from './Case';
import Publish from '../modal/Publish';
import RollBack from '../modal/RollBack';
const { Panel } = Collapse;
const { TabPane } = Tabs;


class RightContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showPublish: false,
      showRollBack: false,
      currentItem: {}
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() { }

  componentDidUpdate(prevProps, prevState) {
    const { releasesActiveInfo } = this.props;
    if (prevProps.releasesActiveInfo !== releasesActiveInfo) {
      if (releasesActiveInfo.length) {
        this.onShowRollBackModal();
      } else {
        message.info('没有可以回滚的发布历史')
      }
    }
  }
  //------------------------事件------------------------------------
  onPublish = (e, item) => {
    e && e.stopPropagation();
    this.setState({
      showPublish: true,
      currentItem: item
    })
  }
  onRollBack = (e, item) => {
    e && e.stopPropagation();
    this.onFetchRollBackReleasesActive(item);
    this.setState({
      // showRollBack: true,
      currentItem: item
    })
  }
  onShowRollBackModal = () => {
    this.setState({
      showRollBack: true
    })
  }
  onFetchRollBackReleasesActive = (item) => {
    const { dispatch } = this.props;
    let baseInfo = item.baseInfo || {};
    dispatch({
      type: 'project/releasesActiveInfo',
      payload: {
        namespaceId: baseInfo.id
      }
    })
  }
  onCancelModal = (type) => {
    this.setState({
      [type]: false
    })
  }
  //发布/回滚成功
  onSaveSuccess = () => {
    this.onFetchNamespaceList();
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
  //------------------------渲染------------------------------------
  renderRightItemHeader = (item) => {
    let baseInfo = item.baseInfo || {};
    return (
      <Row type="flex" justify="space-between">
        <Col>
          <Row type="flex" gutter={8}>
            <Col>{baseInfo.namespaceName}</Col>
            <Col>
              <Tag color="#2db7f5">{item.public ? '公共' : '私有'}</Tag>
              <Tag color="#87d068">{item.format}</Tag>
            </Col>
          </Row>
        </Col>
        <Col>
          <Row type="flex" gutter={8}>
            <Col>
              <Button type="primary" size="small" onClick={(e) => this.onPublish(e, item)}>发布</Button>
            </Col>
            <Col>
              <Button size="small" onClick={(e) => this.onRollBack(e, item)}>回滚</Button>
            </Col>
            <Col>
              <Button size="small">发布历史</Button>
            </Col>
            {/* <Col>
              <Button size="small">灰度</Button>
            </Col> */}
            <Col>
              <Dropdown overlay={
                <Menu>
                  <Menu.Item key="1">
                    灰度
                  </Menu.Item>
                  <Menu.Item key="2">
                    删除
                  </Menu.Item>
                </Menu>
              } placement="bottomLeft">
                <Button size="small">
                  <Icon type="ellipsis" />
                </Button>

              </Dropdown>
            </Col>
          </Row>
        </Col>
      </Row>
    )
  }
  renderItem(item, i) {
    let baseInfo = item.baseInfo || {};
    return (
      <Fragment>
        <Collapse bordered={false} defaultActiveKey={baseInfo.id} key={baseInfo.id}>
          <Panel
            key={baseInfo.id}
            header={this.renderRightItemHeader(item)}
            // extra={this.rendeRightItemExtra()}
            style={{ marginBottom: 20, backgroundColor: '#fff' }}
          >
            <Tabs>
              {
                item.format === 'properties' &&
                <TabPane tab="表格" key="1">
                  <TableList tableList={item.items} baseInfo={baseInfo}/>
                </TabPane>
              }
              <TabPane tab="文本" key="2">
                <TextContent text={item} />
              </TabPane>
              <TabPane tab="更改历史" key="3">
                <History />
              </TabPane>
              <TabPane tab="实例列表" key="4">
                <Case />
              </TabPane>
            </Tabs>
          </Panel>
        </Collapse>
        {this.renderOpaModal(baseInfo)}
      </Fragment>
    )
  }
  renderOpaModal(baseInfo) {
    const { showPublish, showRollBack, currentItem } = this.state;
    console.log('baseInfo--->', baseInfo)
    return (
      <Fragment>
        {
          showPublish && <Publish onCancel={() => this.onCancelModal('showPublish')} onSave={this.onSaveSuccess} currentItem={currentItem} baseInfo={baseInfo}/>
        }
        {
          showRollBack && <RollBack onCancel={() => this.onCancelModal('showRollBack')} onSave={this.onSaveSuccess} currentItem={currentItem} />
        }
      </Fragment>
    )
  }
  render() {
    const { list, loading } = this.props;
    return (
      <div className={styles.detailRightBox}>
        {
          loading ?
            <Loading /> :
            <Fragment>
              {
                list.length ?
                  <Fragment>
                    {
                      list.map((item, i) => this.renderItem(item, i))
                    }
                  </Fragment>
                  :
                  <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              }
            </Fragment>
        }

      </div>
    );
  }
}

export default connect(({ project, loading }) => ({
  list: project.nameSpaceList,
  appDetail: project.appDetail,
  currentEnv: project.currentEnv,
  releasesActiveInfo: project.releasesActiveInfo,
  loading: loading.effects["project/nameSpaceList"]
}))(RightContent);

