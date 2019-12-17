import React, { Fragment } from 'react';
import { Card, Button, Descriptions, Icon, Row, Col, Menu} from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import CreateProject from '../create/';
import styles from '../index.less';
import RightContent from './rightContent/';

const { SubMenu } = Menu;

class ProjectDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showProjectEdit: false,
      list: [{}, {}, {}]
    };
  }
  //------------------------生命周期--------------------------------
  componentDidMount() { }
  //------------------------事件------------------------------------
  onCancel = () => {
    this.setState({
      showProjectEdit: false
    })
  }
  onShowProjectEdit = () => {
    this.setState({
      showProjectEdit: true
    })
  }
  onSave = () => {
    console.log('onSave-->修改信息成功')
  }
  onEnvClick = (e) => {
    console.log('e-->', e)
  }
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
      <a onClick={this.onShowProjectEdit}>
        <Icon type="edit" theme="twoTone" style={{ fontSize: 20 }} title="编辑项目" />
      </a>
    )
  }
  renderEnv() {
    return (
      <Card title="环境列表">
        <Menu mode="inline" onClick={this.onEnvClick} style={{ width: '100%' }}>
          <Menu.Item key="1">环境1</Menu.Item>
          <SubMenu title="环境2">
            <Menu.Item key="2">Option 1</Menu.Item>
            <Menu.Item key="3">Option 2</Menu.Item>
          </SubMenu>
        </Menu>
      </Card>
    )
  }
  renderOpe() {
    return (
      <Card className={styles.marginTop20} title="操作">
        <Button block type="dashed">+ 添加集群</Button>
        <Button block className={styles.marginTop20} type="dashed">+ 添加命名空间</Button>
        <Button block className={styles.marginTop20} type="dashed">命名空间管理</Button>
      </Card>
    )
  }
 
  render() {
    const { showProjectEdit } = this.state;
    return (
      <PageHeaderWrapper title="项目信息" content={this.renderBaseInfo()} extra={this.renderEdit()}>
        <Row type="flex" gutter={24}>
          <Col span={6}>
            {this.renderEnv()}
            {this.renderOpe()}
          </Col>
          <Col span={18}>
            <RightContent />
          </Col>
        </Row>
        {
          showProjectEdit && <CreateProject onCancel={this.onCancel} onSave={this.onSave} />
        }
      </PageHeaderWrapper>
    );
  }
}
export default ProjectDetail;
