import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message } from 'antd';
import { system } from '@/services/system';

const FormItem = Form.Item;
const { TextArea } = Input;
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
class EditParams extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  onSubmit = (e) => {
    const { currentItem } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        this.onEdit(values)
      }
    });
  }

  onEdit = async (values) => {
    const { onCancel, onSave, currentItem } = this.props;
    let res = await system.serverConfigEdit({ ...values, id: currentItem.id });
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
    const { currentItem } = this.props;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="key">
          {getFieldDecorator('key', {
            initialValue: currentItem.key,
            rules: [
              { required: true, message: "请输入key值" }
            ]
          })(<Input placeholder="请输入key值" disabled/>)}
        </FormItem>
        <FormItem label="value">
          {getFieldDecorator('value', {
            initialValue: currentItem.value,
            rules: [
              { required: true, message: "请输入value值" }
            ]
          })(<Input placeholder="请输入value值" />)}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('comment', {
            initialValue: currentItem.comment,
            // rules: [
            //   { required: true, }
            // ]
          })(<TextArea placeholder="请输入备注" rows={4} />)}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel, currentItem } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={'编辑系统参数'}
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

}))(EditParams));