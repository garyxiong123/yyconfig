import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Tree, message, Checkbox, Row, Col } from 'antd';
import { project } from '@/services/project';

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
      // appEnvClusterNamespaceIds: [],
      checkList: []
    };
  }
  componentDidMount() { }

  onSubmit = (e) => {
    const { onCancel, currentItem } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('values-->', values)
        this.onConfigAdd(values);
      }
    })
  }

  onConfigAdd = async (values) => {
    const { checkList } = this.state;
    let res = await project.configAdd({
      appEnvClusterNamespaceIds:checkList,
      ...values
    });
    if(res && res.code === '1') {
      message.success('新建成功');
      this.onSuccess();
    }
  }
  onSuccess = (res) => {
    const { onSave } = this.props;
    if (res && res.code === '1') {
      message.success('操作成功');
      onCancel();
      onSave();
    }
  }

  onChange = (e) => {
    const { checkList } = this.state;
    let target = e.target, list = checkList;
    if (e.target.checked) {
      list.push(e.target.value)
    } else {
      let index = list.indexOf(e.target.value);
      list.splice(index, 1)
    }
    this.setState({
      checkList: list
    })
    // this.setState({
    //   checkList: [
    //     ...checkList,
    //     ...checkedValues
    //   ]
    // })
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { envList, currentItem } = this.props;
    let item = currentItem.item || {};
    const { checkList } = this.state;
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
            {
              envList.map((item, i) => (
                <Fragment key={item.env}>
                  <div>{item.env}(环境)</div>
                  <Checkbox.Group style={{ width: '100%', marginLeft: 15 }}>
                    <Row type="flex">
                      {
                        item.clusters && item.clusters.map((vo) => (
                          <Col span={6} key={vo.id}>
                            <Checkbox value={vo.id} onChange={(e) => this.onChange(e, item)}>{vo.name}</Checkbox>
                          </Col>
                        ))
                      }
                    </Row>
                  </Checkbox.Group>
                </Fragment>
              ))
            }
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