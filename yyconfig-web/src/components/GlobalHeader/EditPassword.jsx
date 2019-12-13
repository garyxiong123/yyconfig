import React from 'react';
import { Modal, Form, Input, message } from 'antd';
import { connect } from 'dva';
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
class EditPassword extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { dispatch } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (values.password !== values.confirmPassword) {
          message.error('两次新密码不一致');
          return;
        }
        this.onModifyPassword(values);
      }
    });
  };
  onModifyPassword = async (values) => {
    const { dispatch, onCancel } = this.props;
    let res = await auth.modifyPassword(values);
    if (res && res.code == '1') {
      onCancel();
      message.success('修改成功');
      dispatch({
        type: 'login/logout',
      });
    }
  }
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit}>
        <FormItem label="旧密码">
          {getFieldDecorator('originPassword', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请输入旧密码", pattern: /^\S*$/ }
            ]
          })(<Input.Password placeholder="请输入旧密码" />)}
        </FormItem>
        <FormItem label="新密码">
          {getFieldDecorator('password', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请输入新密码" }
            ]
          })(<Input.Password placeholder="请输入新密码" />)}
        </FormItem>
        <FormItem label="确认密码">
          {getFieldDecorator('confirmPassword', {
            initialValue: undefined,
            rules: [
              { required: true, message: "请确认新密码(不含空格)", pattern: /^\S*$/ }
            ]
          })(<Input.Password placeholder="请确认新密码" />)}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    return (
      <Modal
        title="修改密码"
        visible={true}
        onOk={this.onSubmit}
        onCancel={onCancel}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}
export default Form.create()(connect(({ }) => ({}))(EditPassword));
