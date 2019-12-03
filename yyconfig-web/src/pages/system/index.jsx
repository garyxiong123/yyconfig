import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import BasicInfo from './BasicInfo';
import Params from './Params';
import DevAuth from './DevAuth';
import PubSpace from './PubSpace';

class System extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',
      tabList: [
        {
          key: '1',
          tab: '系统信息'
        },
        {
          key: '2',
          tab: '系统参数'
        },
        {
          key: '3',
          tab: '开发授权平台管理'
        },
        {
          key: '4',
          tab: '公共命名空间类型管理'
        },
      ],
    };
  }
  componentDidMount() {}
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
          key === '1' && <BasicInfo />
        }
        {
          key === '2' && <Params />
        }
        {
          key === '3' && <DevAuth />
        }
        {
          key === '4' && <PubSpace />
        }
      </PageHeaderWrapper>
    );
  }
}
export default System;
