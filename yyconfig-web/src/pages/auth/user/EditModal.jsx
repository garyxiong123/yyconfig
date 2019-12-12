import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input } from 'antd';


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
      detail: {}
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { onOk } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onOk(values);
      }
    });
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { detail } = this.state;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="用户名">
          {getFieldDecorator('username', {
            initialValue: detail.username,
            rules: [
              { required: true, message: "用户名为14位以内数字字母下划线的组合", pattern: /^\w{1,14}$/ }
            ]
          })(<Input placeholder="请输入用户名" autoComplete="new-password" />)}
        </FormItem>
        <FormItem label="全名">
          {getFieldDecorator('realName', {
            initialValue: detail.realName,
            rules: [
              { required: true, message: "请输入32位以下中文字母标点符号的组合", pattern: /^[a-zA-Z,.?;:，。“”！（）？\u4E00-\u9FA5]{1,32}$/, }
            ]
          })(<Input placeholder="请输入真实姓名" />)}
        </FormItem>
        <FormItem label="邮箱">
          {getFieldDecorator('email', {
            initialValue: detail.email,
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
      </Form>
    )
  }
  render() {
    const { visible, onCancel, currentUser } = this.props;
    return (
      <Modal
        title={currentUser.id ? '编辑用户' : '新增用户'}
        visible={visible}
        onOk={this.onSubmit}
        onCancel={onCancel}
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

