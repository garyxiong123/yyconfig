import React from 'react';
import { connect } from 'dva';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Select, Input, Button, Row, Col, Modal, message } from 'antd';
import styles from '../index.less';
import { project } from '@/services/project';


const FormItem = Form.Item;
const { Option } = Select;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 6 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};

class CreateProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false
    };
  }
  componentDidMount() {
    const { departmentList, appId } = this.props;
    if (!departmentList.length) {
      this.onFetchDepartmentList()
    }
  }
  onFetchDepartmentList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'auth/departmentList',
      payload: {}
    })
  }
  onSubmit = (e) => {
    const { appId } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        if (appId) {
          this.onEditProject({ ...values, appId })
        } else {
          this.onAddProject(values)
        }
        this.setState({
          loading: false
        })
      }
    })
  }
  onAddProject = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await project.projectAdd(values);
    if (res && res.code == '1') {
      message.success('添加成功');
      onCancel();
      onSave();
    }
  }
  onEditProject = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await project.projectEdit(values);
    if (res && res.code == '1') {
      message.success('修改成功');
      onCancel();
      onSave();
    }
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { departmentList, userListAll } = this.props;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="部门">
          {getFieldDecorator('orgId', {
            // initialValue:  undefined,
            rules: [
              { required: true, message: '请选择部门' }
            ]
          })(
            <Select placeholder="请选择部门" showSearch>
              {
                departmentList.map((item, i) => (
                  <Option value={item.id} key={item.id}>{item.name}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>
        <FormItem label="项目Id">
          {getFieldDecorator('appCode', {
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
          {getFieldDecorator('ownerId', {
            rules: [
              { required: true, message: '请选择项目负责人' }
            ]
          })(
            <Select placeholder="请选择项目负责人" showSearch>
              {
                userListAll.map((item) => (
                  <Option value={item.id} key={item.id}>{item.realName}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>
        <FormItem label="项目管理员">
          {getFieldDecorator('admins', {
            // rules: [
            //   { required: true, message: '请选择项目管理员' }
            // ]
          })(
            <Select placeholder="请选择项目管理员" mode="multiple" showSearch allowClear>
              {
                userListAll.map((item) => (
                  <Option value={item.id} key={item.id}>{item.realName}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title="创建项目"
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={loading}
      >
        {this.renderForm()}
      </Modal>

    );
  }
}

export default Form.create()(connect(({ auth }) => ({
  departmentList: auth.departmentList,
  userListAll: auth.userListAll
}))(CreateProject));
