import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input } from 'antd';

const FormItem = Form.Item;
const { TextArea } = Input;
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

class Publish extends React.Component {
  constructor(props) {
    super(props);
    this.state = {

    };
  }
  componentDidMount() { }


  onSubmit = (e) => {
    const { onCancel } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // this.setState({
        //   loading: true
        // }}
        onCancel();
      }
    })
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="Changes">
           <span>配置没有变化</span>
        </FormItem>
        <FormItem label="Release Name">
          {getFieldDecorator('releaseName', {
            // initialValue: 'appId',
            rules: [
              { required: true, message: '请输入Release Name' }
            ]
          })(
            <Input placeholder="请输入Release Name" />
          )}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('comment', {
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    return (
      <Modal
        title={"发布"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        okText="发布"
        width={800}
      >
        {this.renderForm()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({

}))(Publish));