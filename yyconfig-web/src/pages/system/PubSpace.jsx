import React from 'react';
import { connect } from 'dva';
import { Card, Table, Button, Divider, Popconfirm, message } from 'antd';
import moment from 'moment';
import { PubSpaceAdd } from './pubSpace/index';
import { system } from '@/services/system';

class PubSpace extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
      showEditModal: false,
      currentItem: {}
    };
  }
  componentDidMount() {
    this.onFetchList()
  }


  onFetchList = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'system/openNamespaceType',
      payload: searchObj
    })
  }

  onEdit = (record = {}) => {
    this.setState({
      currentItem: record,
      showEditModal: true
    })
  }
  onCancel = () => {
    this.setState({
      showEditModal: false
    })
  }
  onDelete = async (id) => {
    let res = await system.openNamespaceTypeDelete({ id });
    if (res && res.code === '1') {
      message.success('刪除成功');
      this.onFetchList();
    }
  }
  onSave = () => {
    this.onFetchList();
  }
  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '备注',
        dataIndex: 'comment',
      },
      {
        title: '修改人',
        dataIndex: 'updateAuthor',
      },
      {
        title: '修改时间',
        dataIndex: 'updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm') : ''}</span>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <div>
            <span>
              <a
                onClick={() => {
                  this.onEdit(record);
                }}
              >
                编辑
              </a>
              <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          </div>
        ),
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={list || []}
        loading={loading}
        pagination={false}
        rowKey={record => {
          return record.id;
        }}
      />
    )
  }
  render() {
    const { showEditModal, currentItem } = this.state;
    return (
      <Card title={
        <Button type="primary" onClick={() => { this.onEdit() }}> + 添加公共命名空间类型</Button>
      }>
        {
          this.renderTable()
        }
        {showEditModal && <PubSpaceAdd onCancel={this.onCancel} currentItem={currentItem} onSave={this.onSave} />}
      </Card>
    );
  }
}


export default connect(({ system, loading }) => ({
  list: system.openNamespaceType,
  loading: loading.effects["auth/openNamespaceType"]
}))(PubSpace);
