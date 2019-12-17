import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message } from 'antd';
import { auth } from '@/services/auth';


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
  componentDidMount() { }

  onSubmit = (e) => {
    const { currentUser } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        if (currentUser.userId) {
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
    let res = await auth.userEdit({ ...values, userId: currentUser.userId });
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
    const { currentUser } = this.props;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="用户名">
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
          currentUser.userId &&
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
          (!currentUser.userId || getFieldValue('resetPas') === 1) &&
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
        title={currentUser.userId ? '编辑用户' : '新增用户'}
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
export default Form.create()(connect(({ }) => ({

}))(UserEditModal));

