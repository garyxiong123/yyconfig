import React from 'react';
import { Card, Tabs } from 'antd';

const { TabPane } = Tabs;

class PublicSpace extends React.Component {
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

  render() {
    const { key } = this.state;
    return (
      <Tabs activeKey={key} onChange={this.onTabChange} type="card">
        <TabPane tab="选项1" key="1">
          选项1
      </TabPane>
        <TabPane tab="选项2" key="2">
          选项2
      </TabPane>
      </Tabs>
    );
  }
}
export default PublicSpace;
