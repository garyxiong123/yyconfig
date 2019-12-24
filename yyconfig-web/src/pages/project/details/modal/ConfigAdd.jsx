import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Tree } from 'antd';

const FormItem = Form.Item;
const { TextArea } = Input;
const { TreeNode } = Tree;
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

class ConfigAdd extends React.Component {
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
        console.log('values-->', values)
        // this.setState({
        //   loading: true
        // }}
        // onCancel();
      }
    })
  }
  onCheck = (checkedKeys, info) => {
    console.log('checkedKeys-->', checkedKeys)
    console.log('info-->', info)
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { envList } = this.props;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="Key">
          {getFieldDecorator('key', {
            // initialValue: 'appId',
            rules: [
              { required: true, message: '请输入Key' }
            ]
          })(
            <Input placeholder="请输入Key" />
          )}
        </FormItem>
        <FormItem label="Value">
          {getFieldDecorator('value', {
            // initialValue: 'appId',
            rules: [
              { required: true, message: '请输入Value' }
            ]
          })(
            <Input placeholder="请输入Value" />
          )}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('comment', {
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
        <FormItem label="选择集群">
          <Tree
            checkable
            selectable={false}
            onCheck={this.onCheck}
          >
            {
              envList.map((item, i) => (
                <TreeNode title={item.env} key={item.env}>
                  {
                    item.clusters && item.clusters.map((vo) => (
                      <TreeNode title={vo.name} key={vo.id} />
                    ))
                  }
                </TreeNode>
              ))
            }
          </Tree>
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    return (
      <Modal
        title={"添加配置"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        width={800}
      >
        {this.renderForm()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({
  envList: project.envList
}))(ConfigAdd));