import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
import User from './User';
import Role from './Role';
import Auth from './Auth';
import Department from './Department';

class AuthMain extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',
      tabList: [
        {
          key: '1',
          tab: '用户'
        },
        // {
        //   key: '2',
        //   tab: '角色'
        // },
        {
          key: '3',
          tab: '权限'
        },
        {
          key: '4',
          tab: '部门管理'
        },
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
          key === '1' && <User />
        }
        {
          key === '2' && <Role />
        }
        {
          key === '3' && <Auth />
        }
        {
          key === '4' && <Department />
        }
      </PageHeaderWrapper>
    );
  }
}
export default AuthMain;
