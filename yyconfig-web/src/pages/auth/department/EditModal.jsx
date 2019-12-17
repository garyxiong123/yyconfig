import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message } from 'antd';
import { department } from '@/services/auth';

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
      loading: false
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { currentItem } = this.props;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        if (currentItem.id) {
          this.onEdit(values)
        } else {
          this.onAdd(values)
        }
      }
    });
  }
  onAdd = async (values) => {
    const { onCancel, onSave } = this.props;
    let res = await department.departmentAdd(values);
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
    const { onCancel, onSave, currentItem } = this.props;
    let res = await department.departmentEdit({ ...values, departmentId: currentItem.id });
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
        <FormItem label="部门名称">
          {getFieldDecorator('name', {
            initialValue: currentItem.name,
            rules: [
              { required: true, message: "请输入部门名称" }
            ]
          })(<Input placeholder="请输入部门名称" />)}
        </FormItem>
        <FormItem label="code">
          {getFieldDecorator('code', {
            initialValue: currentItem.code,
            rules: [
              { required: true, message: "请输入code" }
            ]
          })(<Input placeholder="请输入code" disabled={currentItem.id ? true : false} />)}
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
        title={currentItem.id ? '编辑部门' : '新增部门'}
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

}))(DepartEditModal));

