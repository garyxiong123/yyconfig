import React from 'react';
import { connect } from 'dva';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Select, Input, Button, Row, Col } from 'antd';
import router from 'umi/router';
import styles from '../index.less';


const FormItem = Form.Item;
const { Option } = Select;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
};

class CreateProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('values-->', values)
      }
    })
  }
  onBack=()=>{
    router.goBack();
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="部门">
          {getFieldDecorator('a', {
            // initialValue:  undefined,
            rules: [
              { required: true, message: '请选择部门' }
            ]
          })(
            <Select placeholder="请选择部门" showSearch>
              <Option value="1">部门1</Option>
            </Select>
          )}
        </FormItem>
        <FormItem label="项目Id">
          {getFieldDecorator('id', {
            rules: [
              { required: true, message: "请输入项目Id" }
            ]
          })(<Input placeholder="请输入项目Id" />)}
        </FormItem>
        <FormItem label="项目名称">
          {getFieldDecorator('name', {
            rules: [
              { required: true, message: "请输入项目名称" }
            ]
          })(<Input placeholder="请输入项目名称" />)}
        </FormItem>
        <FormItem label="项目负责人">
          {getFieldDecorator('b', {
            rules: [
              { required: true, message: '请选择项目负责人' }
            ]
          })(
            <Select placeholder="请选择项目负责人" showSearch>
              <Option value="1">项目负责人1</Option>
            </Select>
          )}
        </FormItem>
        <FormItem label="项目管理员">
          {getFieldDecorator('c', {
            // rules: [
            //   { required: true, message: '请选择项目管理员' }
            // ]
          })(
            <Select placeholder="请选择项目管理员" mode="multiple" showSearch allowClear>
              <Option value="1">项目管理员1</Option>
              <Option value="2">项目管理员2</Option>
            </Select>
          )}
        </FormItem>
      </Form>
    )
  }
  renderSave() {
    return (
      <Row type="flex" justify="center">
        <Col>
          <Button onClick={this.onBack}>返回</Button>
        </Col>
        <Col>
          <Button type="primary" className={styles.buttonMarginL}>确定</Button>
        </Col>
      </Row>
    )
  }
  render() {
    return (
      <PageHeaderWrapper subTitle="创建项目" title=" ">
        <Card>
          {this.renderForm()}
          {this.renderSave()}
        </Card>
      </PageHeaderWrapper>

    );
  }
}

export default Form.create()(connect(({ }) => ({
}))(CreateProject));
