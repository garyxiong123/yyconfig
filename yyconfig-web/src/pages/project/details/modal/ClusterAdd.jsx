import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Checkbox } from 'antd';
import { project } from '@/services/project';

const FormItem = Form.Item;
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

class ClusterAdd extends React.Component {
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
    const { envList } = this.props;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="项目Id">
          {getFieldDecorator('appId', {
            initialValue: 'appId',
            rules: [
              { required: true, message: '请输入项目Id' }
            ]
          })(
            <Input placeholder="请输入项目Id" disabled />
          )}
        </FormItem>
        <FormItem label="集群名称">
          {getFieldDecorator('name', {
            // initialValue: '',
            rules: [
              { required: true, message: '请输入集群名称' }
            ]
          })(
            <Input placeholder="请输入集群名称" />
          )}
        </FormItem>
        <FormItem label="选择环境">
          {getFieldDecorator('envList', {
            rules: [
              { required: true, message: "至少选择一个环境" }
            ]
          })(
            <Checkbox.Group>
              {
                envList && envList.map((item, i) => (
                  <Checkbox value={item.env} key={item.env}>{item.env}</Checkbox>
                ))
              }
            </Checkbox.Group>
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    return (
      <Modal
        title={"添加集群"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
      >
        {this.renderForm()}
      </Modal>
    );
  }
}

export default Form.create()(connect(({ project }) => ({
  envList: project.envList
}))(ClusterAdd));
