import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message, Radio, Select } from 'antd';
import { auth } from '@/services/auth';


const { Option } = Select;
const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 5 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
  },
};

class UserEditModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false
    };
  }
  componentDidMount() {
    const { departmentList } = this.props;
    if (!departmentList.length) {
      this.onFetchDepartmentList();
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
    const { currentUser } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        if (currentUser.id) {
          this.onEdit(values)
        } else {
          this.onAdd(values)
        }
      }
    });
  }
  onAdd = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await auth.userAdd(values);
    if (res && res.code == '1') {
      message.success('添加成功');
      onCancel();
      onSave();
    }
    this.setState({
      loading: false
    })
  }
  onEdit = async (values) => {
    const { onCancel, onSave, currentUser } = this.props;
    let res = await auth.userEdit({ ...values, id: currentUser.id });
    if (res && res.code == '1') {
      message.success('修改成功');
      onCancel();
      onSave();
    }
    this.setState({
      loading: false
    })
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { currentUser, departmentList } = this.props;
    let department = currentUser.department || {};
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="用户名" autoComplete="off">
          {getFieldDecorator('username', {
            initialValue: currentUser.username,
            rules: [
              { required: true, message: "用户名为14位以内数字字母下划线的组合", pattern: /^\w{1,14}$/ }
            ]
          })(<Input placeholder="请输入用户名" autoComplete="new-password" />)}
        </FormItem>
        <FormItem label="全名">
          {getFieldDecorator('realName', {
            initialValue: currentUser.realName,
            rules: [
              { required: true, message: "请输入32位以下中文字母标点符号的组合", pattern: /^[a-zA-Z,.?;:，。“”！（）？\u4E00-\u9FA5]{1,32}$/, }
            ]
          })(<Input placeholder="请输入真实姓名" />)}
        </FormItem>
        <FormItem label="部门">
          {getFieldDecorator('departmentId', {
            initialValue: department.id,
            rules: [
              { required: true, message: '请选择部门' }
            ]
          })(
            <Select
              placeholder="请选择部门"
            >
              {
                departmentList && departmentList.map((item) => (
                  <Option value={item.id} key={item.id}>{item.name}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>
        <FormItem label="邮箱">
          {getFieldDecorator('email', {
            initialValue: currentUser.email,
            rules: [
              {
                required: true,
                message: '邮箱格式不正确',
                pattern: /^\S+@{1}\S+[.]{1}\S+$/,

              }, {
                validator(rule, value, callback) {
                  try {
                    if (value.length > 100) {
                      callback('最多100个字符')
                    } else {
                      callback()
                    }
                  } catch (err) {
                    callback()
                  }
                }
              }
            ]
          })(<Input placeholder="请输入邮箱" />)}
        </FormItem>
        {
          currentUser.id &&
          <FormItem label="是否重置密码">
            {getFieldDecorator('resetPas', {
              initialValue: 0,
              rules: [
                { required: false }
              ]
            })(
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            )}
          </FormItem>
        }
        {
          (!currentUser.id || getFieldValue('resetPas') === 1) &&
          <FormItem label="密码">
            {getFieldDecorator('password', {
              initialValue: currentUser.password,
              rules: [
                { required: true, message: "请输入密码(不含空格)", pattern: /^\S*$/ }
              ]
            })(<Input.Password placeholder="请输入密码" autoComplete="new-password" />)}
          </FormItem>
        }
      </Form>
    )
  }
  render() {
    const { onCancel, currentUser } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={currentUser.id ? '编辑用户' : '新增用户'}
        visible={true}
        onOk={this.onSubmit}
        onCancel={onCancel}
        confirmLoading={loading}
      >
        {
          this.renderForm()
        }
      </Modal>
    );
  }
}
export default Form.create()(connect(({ auth }) => ({
  departmentList: auth.departmentList,
}))(UserEditModal));

