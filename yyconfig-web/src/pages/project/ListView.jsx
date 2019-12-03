import React, { Fragment } from 'react';
import { Card, Tabs, Row, Col, Radio, Icon, Button } from 'antd';
import { MyProject, PubLicSpace } from './listView/'
import styles from './index.less';

const { TabPane } = Tabs;

class ListView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',

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
      <Card>
        <Tabs tabPosition="left" activeKey={key} onChange={this.onTabChange}>
          <TabPane tab="我的项目" key="1">
            <MyProject />
          </TabPane>
          <TabPane tab="公共命名空间列表" key="2">
            <PubLicSpace />
          </TabPane>
        </Tabs>
      </Card>
    );
  }
}
export default ListView;
