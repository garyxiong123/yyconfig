import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message, Table } from 'antd';
import moment from 'moment';
import { project } from '@/services/project';

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
      loading: false,
      changeList: []
    };
  }
  componentDidMount() {

    let releaseTitle = moment().format('YYYYMMDDHHMMSS');
    this.setState({
      releaseTitle
    })
    this.onGetChanges();

  }
  onGetChanges = () => {
    const { currentItem } = this.props;
    let item = currentItem.items || [];
    let changeList = item.filter(vo => (vo.modified || vo.deleted));
    this.setState({
      changeList
    })
  }

  onSubmit = (e) => {
    const { onCancel } = this.props;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true
        })
        this.onPublich(values)
      }
    })
  }
  onPublich = async (values) => {
    const { onSave, currentItem, onCancel } = this.props;
    let baseInfo = currentItem.baseInfo || {};
    let params = { ...values, emergencyPublish: false, appEnvClusterNamespaceId: baseInfo.id };
    let res = await project.createRelease(params);
    if (res && res.code === '1') {
      message.success('发布成功');
      onCancel();
      onSave();
    }
    this.setState({
      loading: false
    })
  }
  renderChangesTable() {
    const { currentItem } = this.props;
    const { changeList } = this.state;
    const columns = [
      {
        title: 'Key',
        dataIndex: 'item.key',
      },
      {
        title: '发布的值',
        dataIndex: 'newValue',
      },
      {
        title: '未发布的值',
        dataIndex: 'oldValue',
      },
      {
        title: '修改人',
        dataIndex: 'item.updateAuthor',
      },
      {
        title: '修改时间',
        dataIndex: 'item.updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD') : ''}</span>
        )
      },
    ];
    return (
      <Table
        bordered
        columns={columns}
        dataSource={changeList || []}
        pagination={false}
        rowKey={record => {
          return record.item.id;
        }}
      />
    )
  }
  renderForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { releaseTitle, changeList } = this.state;
    return (
      <Form onSubmit={this.onSubmit} {...formItemLayout}>
        <FormItem label="Changes">
          {
            changeList.length ?
              this.renderChangesTable() :
              <span>配置没有变化</span>
          }
        </FormItem>
        <FormItem label="Release Name">
          {getFieldDecorator('releaseTitle', {
            initialValue: `${releaseTitle}-release`,
            rules: [
              { required: true, message: '请输入Release Name' }
            ]
          })(
            <Input placeholder="请输入Release Name" />
          )}
        </FormItem>
        <FormItem label="备注">
          {getFieldDecorator('releaseComment', {
          })(
            <TextArea placeholder="请输入备注" rows={4} />
          )}
        </FormItem>
      </Form>
    )
  }
  render() {
    const { onCancel } = this.props;
    const { loading } = this.state;
    return (
      <Modal
        title={"发布"}
        visible={true}
        onCancel={onCancel}
        onOk={this.onSubmit}
        okText="发布"
        width={1000}
        confirmLoading={loading}
      >
        {this.renderForm()}
      </Modal>
    )
  }
}
export default Form.create()(connect(({ project }) => ({

}))(Publish));