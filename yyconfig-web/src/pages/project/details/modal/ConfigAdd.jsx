import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Tree, message, Checkbox, Row, Col } from 'antd';
import { project } from '@/services/project';
import { isTSExpressionWithTypeArguments } from '@babel/types';

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
  componentDidMount() {
    this.onFetchNameSpaceListWithApp();
  }
  onFetchNameSpaceListWithApp = () => {
    const { dispatch, appDetail, baseInfo } = this.props;
    const info = baseInfo || {};
    dispatch({
      type: 'project/nameSpaceListWithApp',
      payload: {
        appCode: info.appCode,
        namespace: info.namespaceName
      }
    })
  }
  onSubmit = (e) => {
    const { onCancel, currentItem } = this.props;
    let item = currentItem.item || {};
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (item.id) {
          //修改
          this.onConfigUpdate(values)
        } else {
          //新增
          this.onConfigAdd(values);
        }
      }
    })
  }

  onConfigAdd = async (values) => {
    const { checkList } = this.state;
    let res = await project.configAdd({
      appEnvClusterNamespaceIds: checkList,
      ...values
    });
    this.onSuccess(res);
  }
  onConfigUpdate = async (values) => {
    const { currentItem } = this.props;
    let item = currentItem.item || {};
    let res = await project.configUpdate({
      itemId: item.id,
      ...values
    });
    this.onSuccess(res);
  }
  onSuccess = (res) => {
    const { onSave, onCancel } = this.props;
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
    const { envList, currentItem, nameSpaceListWithApp } = this.props;
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
            <Input placeholder="请输入Key" disabled={item.id ? true : false} />
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
            {/* {
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
            } */}
            <Checkbox.Group>
              <Row type="flex">
                {
                  nameSpaceListWithApp.map((vo, i) => (
                    <Col span={24} key={vo.id} style={{ marginBottom: 15 }}>
                      <Checkbox value={vo.id} onChange={(e) => this.onChange(e, item)}>{vo.env} - {vo.name}</Checkbox>
                    </Col>

                  ))
                }
              </Row>
            </Checkbox.Group>
          </FormItem>
        }
      </Form>
    )
  }
  render() {
    const { onCancel, currentItem, loading } = this.props;
    let item = currentItem.item || {};
    return (
      <Modal
        title={item.id ? "修改配置" : "添加配置"}
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
  envList: project.envList,
  nameSpaceListWithApp: project.nameSpaceListWithApp,
  appDetail: project.appDetail
}))(ConfigAdd));