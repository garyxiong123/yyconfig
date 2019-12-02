import React, { Fragment } from 'react';
import { Card, Tabs } from 'antd';

const { TabPane } = Tabs;

class ListView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1'
    };
  }
  componentDidMount() { }
  onTabChange = (key) => {
    this.setState({
      key
    })
  }
  renderProject() {
    return (
      <div>
        我的项目
      </div>
    )
  }
  renderSpaceList() {
    return (
      <div>
        公共命名空间列表
      </div>
    )
  }
  render() {
    const { key } = this.state;
    return (
      <Card>
        <Tabs tabPosition="left" activeKey={key} onChange={this.onTabChange}>
          <TabPane tab="我的项目" key="1">
            {this.renderProject()}
          </TabPane>
          <TabPane tab="公共命名空间列表" key="2">
            {this.renderSpaceList()}
          </TabPane>
        </Tabs>
      </Card>
    );
  }
}
export default ListView;
