import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Tree, message } from 'antd';

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
      loading: false,
      appEnvClusterNamespaceIds: []
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { onCancel, currentItem } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('values-->', values)
        // onCancel();
      }
    })
  }
  onSuccess = (res) => {
    const { onSave } = this.props;
    if (res && res.code === '1') {
      message.success('操作成功');
      onCancel();
      onSave();
    }
  }
  onCheck = (checkedKeys, info) => {
    // let checkedKeysCopy = checkedKeys;
    // checkedKeysCopy.pop();
    // this.setState({
    //   appEnvClusterNamespaceIds: checkedKeysCopy
    // })
    // console.log('ids-->', checkedKeysCopy)
    console.log('checkedKeys-->', checkedKeys)

    console.log('info-->', info)
  }
  onSelectCluster = (vo) => {
    console.log('onSelectCluster--->', vo)
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { envList, currentItem } = this.props;
    let item = currentItem.item || {};
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="Key">
          {getFieldDecorator('key', {
            initialValue: item.key,
            rules: [
              { required: true, message: '请输入Key' }
            ]
          })(
            <Input placeholder="请输入Key" />
          )}
        </FormItem>
        <FormItem label="Value">
          {getFieldDecorator('value', {
            initialValue: item.value,
            rules: [
              { required: true, message: '请输入Value' }
            ]
          })(
            <Input placeholder="请输入Value" />
          )}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('comment', {
            initialValue: item.comment,
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
        {
          !item.id &&
          <FormItem label="选择集群">
            <Tree
              checkable
              selectable={false}
              onCheck={this.onCheck}
              defaultExpandAll
            >
              {
                envList.map((item, i) => (
                  <TreeNode title={item.env} key={item.env}>
                    {
                      item.clusters && item.clusters.map((vo) => (
                        <TreeNode title={vo.name} key={vo.id}/>
                      ))
                    }
                  </TreeNode>
                ))
              }
            </Tree>
          </FormItem>
        }
      </Form>
    )
  }
  render() {
    const { onCancel, currentItem, loading } = this.props;
    return (
      <Modal
        title={"添加配置"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        width={800}
        confirmLoading={loading}
      >
        {this.renderForm()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({
  envList: project.envList
}))(ConfigAdd));