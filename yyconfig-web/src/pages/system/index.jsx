import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card } from 'antd';
class System extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() {}
  render() {
    return (
      <PageHeaderWrapper>
        <Card>系统</Card>
      </PageHeaderWrapper>
    );
  }
}
export default System;
