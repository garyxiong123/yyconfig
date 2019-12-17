import React, { Fragment } from 'react';
import { Button, Icon, Collapse, Tabs, Table } from 'antd';
import styles from '../../index.less';

const { Panel } = Collapse;
const { TabPane } = Tabs;


class RightContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [{}, {}, {}],
      tableList: [{},{},{}]
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() { }
  //------------------------事件------------------------------------

  //------------------------渲染------------------------------------
  renderRightItemHeader = () => {
    return (
      <Fragment>
        <span>name</span>
      </Fragment>
    )
  }
  rendeRightItemExtra = () => {
    return (
      <Fragment>
        <Button size="small">发布</Button>
      </Fragment>
    )
  }
  renderTable() {
    const { tableList } = this.state;
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
      },
      {
        title: '全名',
        dataIndex: 'realName',
      }
    ];
    return (
      <Table
        columns={columns}
        dataSource={tableList || []}
        // onChange={this.onTableChange}
        loading={loading}
        pagination={false}
        // rowKey={record => {
        //   return record.userId;
        // }}
      />
    )
  }
  render() {
    const { list } = this.state;
    return (
      <div className={styles.detailRightBox}>
        <Collapse bordered={false}>
          {
            list.map((item, i) => (
              <Panel
                key={i}
                header={this.renderRightItemHeader()}
                extra={this.rendeRightItemExtra()}
                style={{ marginBottom: 20, backgroundColor: '#fff' }}
              >
                <Tabs>
                  <TabPane tab="文本" key="1">
                    文本
                </TabPane>
                  <TabPane tab="表格" key="2">
                    表格
                </TabPane>
                  <TabPane tab="更改历史" key="3">
                    更改历史
                </TabPane>
                  <TabPane tab="实例列表" key="4">
                    实例列表
                </TabPane>
                </Tabs>
              </Panel>
            ))
          }
        </Collapse>
      </div>
    );
  }
}
export default RightContent;
