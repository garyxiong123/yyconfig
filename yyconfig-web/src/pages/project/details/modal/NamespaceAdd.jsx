import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Modal, Form, Radio, Input, Select, Transfer, message, Button } from 'antd';
import { project } from '@/services/project';
import styles from '../../index.less';

const FormItem = Form.Item;
const { Option } = Select;
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

class NamespaceAdd extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentType: 'relation',
      namespaceList: [
        {
          id: 1,
          value: 'namespace1'
        },
        {
          id: 2,
          value: 'namespace2'
        }
      ],
      leftNoSelect: [],
      leftKeys: [],
      rightKeys: [],
      loading: false,
      searchObj: {
        page: 1,
        size: 10
      }
    };
  }
  componentDidMount() {
    const { appList, openNamespaceTypeList } = this.props;
    if (!appList.rows) {
      this.onFetchApplist();
    } else {
      this.onSetleftNoSelect();
      this.setState({
        page: appList.pageNum
      })
    }
    if (!openNamespaceTypeList.length) {
      this.onFetchOpenNamespaceList();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { appList } = this.props;
    const { searchObj } = this.state;
    if (prevProps.appList !== appList) {
      this.onSetleftNoSelect()
    }
    if (prevState.searchObj !== searchObj) {
      this.onFetchApplist()
    }
  }

  onSetleftNoSelect = () => {
    const { appList } = this.props;
    this.setState({
      leftNoSelect: appList.rows
    })
  }
  //获取项目列表
  onFetchApplist = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'project/appList',
      payload: searchObj
    })
  }
  //获取公共命名空间类型列表
  onFetchOpenNamespaceList = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'system/openNamespaceType',
      payload: searchObj
    })
  }

  onChange = (e) => {
    this.setState({
      currentType: e.target.value
    })
  }
  onServiceChange = (targetKeys, direction, moveKeys) => {
    this.setState({ rightKeys: targetKeys })
  }
  onServiceSelectChange = (sourceSelectedKeys, targetSelectedKeys) => {
    this.setState({ leftKeys: [...sourceSelectedKeys, ...targetSelectedKeys] });
  }
  onRelationSubmit = (e) => {
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('onCreateSubmit-values--->', values)
      }
    });
  }
  onCreateSubmit = (e) => {
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('onCreateSubmit-values--->', values)
        this.setState({
          loading: true
        })
        this.onCreateSave(values)
      }
    });
  }
  onCreateSave = async (values) => {
    const { appDetail, onCancel } = this.props;
    const { rightKeys } = this.state;
    let type = values.type;
    console.log('onCreateSave--->', rightKeys)
    switch (type) {
      case 'public': {
        let res = await project.nameSpacePublicAdd({ ...values, appId: appDetail.id });
        this.onSuccess(res);
      } break;
      case 'protect': {
        let res = await project.nameSpaceProtectAdd({ ...values, appId: appDetail.id, authorizedApp: rightKeys });
        this.onSuccess(res);
      } break;
      case 'private': {
        let res = await project.nameSpacePrivateAdd({ ...values, appId: appDetail.id });
        this.onSuccess(res);
      } break;
    }
  }
  onSuccess = (res) => {
    const { onCancel, onSave } = this.props;
    if (res && res.code === '1') {
      message.success('添加成功');
      onCancel();
      onSave()
    }
    this.setState({
      loading: false
    })
  }
  // 服务加载更多
  onMore = () => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        page: searchObj.page + 1
      }
    })
  }
  renderRelation() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { appDetail } = this.props;
    const { namespaceList } = this.state;
    return (
      <Form onSubmit={this.onRelationSubmit} {...formItemLayout}>
        <FormItem label="项目Id">
          {getFieldDecorator('appId', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入项目Id' }
            ]
          })(
            <Input placeholder="请输入项目Id" disabled />
          )}
        </FormItem>
        <FormItem label="namespace">
          {getFieldDecorator('namespace', {
            // initialValue: undefined,
            rules: [
              { required: true, message: '请选择namespace' }
            ]
          })(
            <Select placeholder="请选择namespace">
              {
                namespaceList.map((item) => (
                  <Option value={item.id} key={item.id}>{item.value}</Option>
                ))
              }
            </Select>
          )}
        </FormItem>

      </Form>
    )
  }
  renderServiceItem() {
    const { leftNoSelect, leftKeys, rightKeys } = this.state;
    console.log('leftNoSelect-->', leftNoSelect)
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
        footer={this.renderServiceItemFooter}
      />
    )
  }
  renderServiceItemFooter = () => {
    const { appList } = this.props;
    return (
      <Button
        size="small"
        style={{ margin: 5 }}
        onClick={() => this.onMore()} disabled={appList.pageNum < appList.totalPage ? false : true}>More</Button>
    )
  }
  renderCreate() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { appDetail, openNamespaceTypeList } = this.props;
    let department = appDetail.department || {};
    return (
      <Form onSubmit={this.onCreateSubmit} {...formItemLayout}>
        <FormItem label="项目Id">
          {getFieldDecorator('appId', {
            initialValue: appDetail.appCode,
            rules: [
              { required: true, message: '请输入项目Id' }
            ]
          })(
            <Input placeholder="请输入项目Id" disabled />
          )}
        </FormItem>
        {
          getFieldValue('type') === 'private' ?
            <FormItem label="名称">
              {getFieldDecorator('name', {
                // initialValue: appDetail.appCode,
                rules: [
                  { required: true, message: '请输入名称' }
                ]
              })(
                <Input placeholder="请输入名称" addonAfter={
                  <Fragment>
                    {
                      getFieldDecorator('format', {
                        initialValue: 'Properties',
                      })(
                        <Select>
                          <Option value="Properties">properties</Option>
                          <Option value="XML">xml</Option>
                          <Option value="JSON">json</Option>
                          <Option value="YAML">yaml</Option>
                        </Select>
                      )
                    }
                  </Fragment>
                } />
              )}
            </FormItem> :
            <FormItem label="名称">
              {getFieldDecorator('name', {
                // initialValue: appDetail.appCode,
                rules: [
                  { required: true, message: '请输入名称' }
                ]
              })(
                <Input placeholder="请输入名称" addonBefore={`${department.code}.`} />
              )}
            </FormItem>
        }

        <FormItem label="类型">
          {getFieldDecorator('type', {
            initialValue: 'public',
            rules: [
              { required: true, message: '请输入名称' }
            ]
          })(
            <Radio.Group>
              <Radio value="public">public</Radio>
              <Radio value="protect">protect</Radio>
              <Radio value="private">private</Radio>
            </Radio.Group>
          )}
        </FormItem>
        {
          (getFieldValue('type') === 'public' || getFieldValue('type') === 'protect') &&
          <FormItem label="公有命名空间类型">
            {getFieldDecorator('openNamespaceTypeId', {
              // initialValue: undefined,
              rules: [
                { required: true, message: '请选择公有命名空间类型' }
              ]
            })(
              <Select placeholder="请选择公有命名空间类型">
                {
                  openNamespaceTypeList.map((item) => (
                    <Option value={item.id} key={item.id}>{item.name}</Option>
                  ))
                }
              </Select>
            )}
          </FormItem>
        }
        {
          getFieldValue('type') === 'protect' &&
          <Fragment>

            <FormItem label=" " colon={false}>
              {this.renderServiceItem()}
            </FormItem>
          </Fragment>
        }
        <FormItem label="备注">
          {getFieldDecorator('comment', {
            // rules: [
            //   { required: true, message: '请输入备注' }
            // ]
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { currentType, loading } = this.state;
    return (
      <Modal
        title="新建Namespace"
        visible={true}
        onCancel={onCancel}
        onOk={currentType === 'relation' ? this.onRelationSubmit : this.onCreateSubmit}
        width={700}
        confirmLoading={loading}
      >
        <div className={styles.marginBottom25} style={{ textAlign: 'right' }}>
          <Radio.Group value={currentType} buttonStyle="solid" onChange={this.onChange}>
            <Radio.Button value="relation">关联公共Namespace</Radio.Button>
            <Radio.Button value="create">创建Namespace</Radio.Button>
          </Radio.Group>
        </div>
        {
          currentType === 'relation' && this.renderRelation()
        }
        {
          currentType === 'create' && this.renderCreate()
        }
      </Modal>
    );
  }
}

export default Form.create()(connect(({ project, system }) => ({
  appDetail: project.appDetail,
  appList: project.appList,
  openNamespaceTypeList: system.openNamespaceType,
}))(NamespaceAdd));