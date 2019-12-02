import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Tabs } from 'antd';
import ListView from './ListView';
import PicView from './PicView';

const { TabPane } = Tabs;

class Project extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',
      tabList: [
        {
          key: '1',
          tab: '列表视图'
        },
        {
          key: '2',
          tab: '导图视图'
        }
      ],
    };
  }
  componentDidMount() { }
  onTabChange = (key) => {
    this.setState({
      key
    })
  }
  render() {
    const { key, tabList } = this.state;
    return (
      <PageHeaderWrapper tabList={tabList} tabActiveKey={key} onTabChange={this.onTabChange} title=" ">
        {
          key === '1' && <ListView />
        }
        {
          key === '2' && <PicView />
        }
      </PageHeaderWrapper>
    );
  }
}
export default Project;
