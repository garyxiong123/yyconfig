import React from 'react';
import { Card, Button, Descriptions, Icon, Row, Col } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';

class ProjectDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() { }
  //------------------------事件------------------------------------

  //------------------------渲染------------------------------------
  renderBaseInfo() {
    return (
      <Descriptions size="small" column={3}>
        <Descriptions.Item label="项目Id">项目Id</Descriptions.Item>
        <Descriptions.Item label="项目名">项目名</Descriptions.Item>
        <Descriptions.Item label="部门">部门</Descriptions.Item>
        <Descriptions.Item label="负责人">负责人1234</Descriptions.Item>
        <Descriptions.Item label="邮箱">123@163.com</Descriptions.Item>
      </Descriptions>
    )
  }
  renderEdit() {
    return (
      <a>
        <Icon type="edit" theme="twoTone" style={{ fontSize: 20 }} title="编辑项目" />
      </a>
    )
  }
  renderEnv() {
    return (
      <Card title="环境列表">

      </Card>
    )
  }
  render() {
    return (
      <PageHeaderWrapper title="项目信息" content={this.renderBaseInfo()} extra={this.renderEdit()}>
        <Row type="flex" gutter={24}>
          <Col span={6}>
            {this.renderEnv()}
          </Col>
          <Col span={18}>
            <Card>
              666
            </Card>
          </Col>
        </Row>
      </PageHeaderWrapper>
    );
  }
}
export default ProjectDetail;
