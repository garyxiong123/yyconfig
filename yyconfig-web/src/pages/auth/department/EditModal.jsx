import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input } from 'antd';


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

class DepartEditModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {

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
    const { currentItem } = this.props;
    return (
      <Form {...formItemLayout} onSubmit={this.onSubmit} autoComplete="off">
        <FormItem label="部门名称">
          {getFieldDecorator('name', {
            initialValue: currentItem.name,
            rules: [
              { required: true, message: "请输入部门名称" }
            ]
          })(<Input placeholder="请输入部门名称" />)}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('remark', {
            initialValue: currentItem.realName,
            // rules: [
            //   { required: true, }
            // ]
          })(<TextArea placeholder="请输入备注" rows={4} />)}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { visible, onCancel, currentItem } = this.props;
    return (
      <Modal
        title={currentItem.id ? '编辑部门' : '新增部门'}
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

}))(DepartEditModal));

