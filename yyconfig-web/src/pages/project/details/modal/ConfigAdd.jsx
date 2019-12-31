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
    const { baseInfo } = this.props;
    this.onFetchNameSpaceListWithApp();
    this.onSelect([baseInfo.id.toString()])
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/clearData',
      payload: {
        nameSpaceListWithApp: []
      }
    })
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
    const { onCancel, currentItem, opeType } = this.props;
    let item = currentItem.item || {};
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (item.id && !opeType) {
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
    let appEnvClusterNamespaceIds = this.onGetAppEnvClusterNamespaceIds();
    let res = await project.configAdd({
      appEnvClusterNamespaceIds,
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
  onGetAppEnvClusterNamespaceIds = () => {
    const { checkList } = this.state;
    let list = checkList, ids = [];
    list.map((vo) => {
      if (vo.indexOf('-') > -1) {
        return
      }
      ids.push(vo)
    })
    return ids
  }
  // onChange = (e) => {
  //   const { checkList } = this.state;
  //   let target = e.target, list = checkList;
  //   if (e.target.checked) {
  //     list.push(e.target.value)
  //   } else {
  //     let index = list.indexOf(e.target.value);
  //     list.splice(index, 1)
  //   }
  //   this.setState({
  //     checkList: list
  //   })
  // }
  onSelect = (keys, e) => {
    this.setState({
      checkList: keys
    })
  }

  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { envList, currentItem, nameSpaceListWithApp, opeType, baseInfo } = this.props;
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
          (!item.id || opeType === 'reCover') &&
          <FormItem label="选择集群">
            {
              nameSpaceListWithApp.length ?
                <Tree
                  checkable
                  defaultExpandAll
                  onCheck={this.onSelect}
                  defaultCheckedKeys={[baseInfo.id.toString()]}
                >
                  <TreeNode title="全选" key={'0-'}>
                    {
                      nameSpaceListWithApp.map((item, i) => (
                        <TreeNode title={item.env} key={`${item.env}-`}>
                          {
                            item.namespaceListResps.map((vo) => (
                              <TreeNode title={vo.name} key={vo.id.toString()} />
                            ))
                          }
                        </TreeNode>
                      ))
                    }
                  </TreeNode>
                </Tree> : null
            }
          </FormItem>
        }
        {/* {
          (!item.id || opeType === 'reCover') &&
          <FormItem label="选择集群">
            {
              nameSpaceListWithApp.map((item, i) => (
                <Fragment key={item.env}>
                  <Checkbox>{item.env}</Checkbox>
                  <Checkbox.Group style={{ width: '100%', marginLeft: 15 }}>
                    <Row type="flex">
                      {
                        item.namespaceListResps && item.namespaceListResps.map((vo) => (
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
        } */}
      </Form>
    )
  }
  render() {
    const { onCancel, currentItem, loading, opeType } = this.props;
    let item = currentItem.item || {};
    return (
      <Modal
        title={opeType === 'reCover' ? '覆盖配置' : item.id ? "修改配置" : "添加配置"}
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
  appDetail: project.appDetail,
  currentEnv: project.currentEnv
}))(ConfigAdd));