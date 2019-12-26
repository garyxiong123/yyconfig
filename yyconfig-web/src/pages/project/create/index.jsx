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
      loading: false,
      appDetail: {}
    };
  }
  componentDidMount() {
    const { departmentList } = this.props;
    if (!departmentList.length) {
      this.onFetchDepartmentList()
    }
    this.onSetAppDetail();
  }

  onSetAppDetail = () => {
    const { appId, appDetail } = this.props;
    if (appId) {
      let appAdminIds = [];
      if (appDetail.appAdmins && appDetail.appAdmins.length) {
        appDetail.appAdmins.map((item) => {
          appAdminIds.push(item.id)
        })
      }
      this.setState({
        appDetail: {
          ...appDetail,
          appAdminIds
        }
      })
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
      }
    })
  }
  onAddProject = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await project.projectAdd(values);
    this.onSuccess(res)
  }
  onEditProject = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await project.projectEdit(values);
    this.onSuccess(res)
  }
  onSuccess = (res) => {
    const { onCancel, onSave } = this.props;
    if (res && res.code == '1') {
      message.success('操作成功');
      onCancel();
      onSave();
    }
    this.setState({
      loading: false
    })
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { departmentList, userListAll } = this.props;
    const { appDetail } = this.state;
    let department = appDetail.department || {}, appOwner = appDetail.appOwner || {};
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="部门">
          {getFieldDecorator('orgId', {
            initialValue: department.id,
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
        <FormItem label="项目Code">
          {getFieldDecorator('appCode', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: "请输入项目Code" }
            ]
          })(<Input placeholder="请输入项目Code" />)}
        </FormItem>
        <FormItem label="项目名称">
          {getFieldDecorator('name', {
            initialValue: appDetail.name,
            rules: [
              { required: true, message: "请输入项目名称" }
            ]
          })(<Input placeholder="请输入项目名称" />)}
        </FormItem>
        <FormItem label="项目负责人">
          {getFieldDecorator('ownerId', {
            initialValue: appOwner.id,
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
            initialValue: appDetail.appAdminIds,
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
    const { onCancel, appId } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={appId ? '修改项目' : '创建项目'}
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

export default Form.create()(connect(({ auth, project }) => ({
  departmentList: auth.departmentList,
  userListAll: auth.userListAll,
  appDetail: project.appDetail
}))(CreateProject));
