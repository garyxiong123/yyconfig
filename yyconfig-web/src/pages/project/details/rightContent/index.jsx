import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Button, Icon, Collapse, Tabs, Table, Spin, Tag, Row, Col, Dropdown, Menu } from 'antd';
import styles from '../../index.less';
import TextContent from './TextContent';
import TableList from './TableList';
import History from './History';
import Case from './Case';

const { Panel } = Collapse;
const { TabPane } = Tabs;


class RightContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() { }
  //------------------------事件------------------------------------
  onPublish(e) {
    e && e.stopPropagation();
    console.log('发布--》')
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
              <Tag color="#87d068">{item.format}</Tag>
            </Col>
          </Row>
        </Col>
        <Col>
          <Row type="flex" gutter={8}>
            <Col>
              <Button type="primary" size="small" onClick={(e)=>this.onPublish(e)}>发布</Button>
            </Col>
            <Col>
              <Button size="small">回滚</Button>
            </Col>
            <Col>
              <Button size="small">发布历史</Button>
            </Col>
            <Col>
              <Button size="small">授权</Button>
            </Col>
            <Col>
              <Button size="small">灰度</Button>
            </Col>
            <Col>
              <Dropdown overlay={
                <Menu>
                  <Menu.Item key="1">
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
              <TableList tableList={item.items} />
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
    )
  }
  render() {
    const { list, loading } = this.props;
    return (
      <div className={styles.detailRightBox}>
        {
          loading ?
            <Spin /> :
            <Collapse bordered={false}>
              {
                list.map((item, i) => this.renderItem(item, i))
              }
            </Collapse>

        }

      </div>
    );
  }
}

export default connect(({ project, loading }) => ({
  list: project.nameSpaceList,
  loading: loading.effects["project/nameSpaceList"]
}))(RightContent);

