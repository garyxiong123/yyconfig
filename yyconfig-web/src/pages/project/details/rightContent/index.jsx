import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Button, Icon, Collapse, Tabs, Table, Spin } from 'antd';
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

  //------------------------渲染------------------------------------
  renderRightItemHeader = (item) => {
    let baseInfo = item.baseInfo || {};
    return (
      <Fragment>
        <span>{baseInfo.namespaceName}</span>
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
  renderItem(item, i) {
    let baseInfo = item.baseInfo || {};
    return (
      <Panel
        key={baseInfo.id}
        header={this.renderRightItemHeader(item)}
        extra={this.rendeRightItemExtra()}
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
            <TextContent />
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

