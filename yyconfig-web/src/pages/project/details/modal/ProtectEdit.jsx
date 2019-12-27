import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message, Table, Transfer } from 'antd';
import { project } from '@/services/project';

class ProtectEdit extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      leftNoSelect: [],
      leftKeys: [],
      rightKeys: [],
    };
  }
  componentDidMount() {
    this.onSetleftNoSelect();
    this.onFetchAssociatedPublicNamespace();
  }
  componentDidUpdate(prevProps, prevState) {
    const { appProtectNamespace } = this.props;
    if (prevProps.appProtectNamespace !== appProtectNamespace) {
      this.onSetRightKeys()
    }
  }

  onSubmit = async () => {
    const { rightKeys } = this.state;
    const { currentItem, appDetail } = this.props;
    let baseInfo = currentItem.baseInfo || {};
    let apps = [];
    rightKeys.map((item)=>(
      apps.push({
        id: item
      })
    ))
    let res = await project.authorizeProtectApp({
      appId: appDetail.id,
      namespace: baseInfo.namespaceName,
      apps
    })
    this.onSuccess(res)
  }
  onSuccess=(res)=>{
    const { onCancel, onSave } = this.props;
    if(res && res.code === '1') {
      message.success('操作成功');
      onSave();
      onCancel();
    }
    this.setState({
      loading: false
    })
  }
  //已授权的项目
  onFetchAssociatedPublicNamespace = () => {
    const { dispatch, currentItem, appDetail } = this.props;
    let baseInfo = currentItem.baseInfo || {};
    dispatch({
      type: 'project/appProtectNamespace',
      payload: {
        appId: appDetail.id,
        namespace: baseInfo.namespaceName
      }
    })
  }
  onSetRightKeys = () => {
    const { appProtectNamespace } = this.props;
    let rightKeys = [], authorizedApp = appProtectNamespace.authorizedApp || [];
    authorizedApp.map((item) => {
      rightKeys.push(item.id)
    })
    this.setState({
      rightKeys
    })
  }
  onSetleftNoSelect = () => {
    const { appListAll } = this.props;
    this.setState({
      leftNoSelect: appListAll
    })
  }

  onServiceChange = (targetKeys, direction, moveKeys) => {
    this.setState({ rightKeys: targetKeys })
  }
  onServiceSelectChange = (sourceSelectedKeys, targetSelectedKeys) => {
    this.setState({ leftKeys: [...sourceSelectedKeys, ...targetSelectedKeys] });
  }

  renderServiceItem() {
    const { leftNoSelect, leftKeys, rightKeys } = this.state;
    return (
      <Transfer
        dataSource={leftNoSelect}
        titles={['待选', '已选']}
        targetKeys={rightKeys}
        selectedKeys={leftKeys}
        onChange={this.onServiceChange}
        onSelectChange={this.onServiceSelectChange}
        render={item => item.name}
        rowKey={record => record.id}
        listStyle={{ width: '300px' }}
      />
    )
  }
  render() {
    const { onCancel } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={"命名空间管理"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        confirmLoading={loading}
        width={700}
      >
        {this.renderServiceItem()}
      </Modal>
    )
  }
}

export default Form.create()(connect(({ project }) => ({
  appListAll: project.appListAll,
  appDetail: project.appDetail,
  appProtectNamespace: project.appProtectNamespace,
}))(ProtectEdit));