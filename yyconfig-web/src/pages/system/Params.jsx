import React from 'react';
import { connect } from 'dva';
import { Card, Table, Button, message } from 'antd';
import moment from 'moment';
import { EditParams } from './params/index';

class Params extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentItem: {},
      showEditModal: false,
    };
  }
  componentDidMount() {
    this.onFetchList();
  }

  onFetchList = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'system/serverConfigList',
      payload: {}
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
  onSave = () => {
    this.onFetchList();
  }

  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: 'key',
        dataIndex: 'key',
      },
      {
        title: 'value',
        dataIndex: 'value',
      },
      {
        title: 'comment',
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
              {/* <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm> */}
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
      <Card title="应用配置" extra={"维护ApolloPortalDB.ServerConfig表数据，如果已存在配置项则会覆盖，否则会创建配置项。配置更新后，一分钟后自动生效"}>
        {
          this.renderTable()
        }
        {showEditModal && <EditParams onCancel={this.onCancel} currentItem={currentItem} onSave={this.onSave} />}
      </Card>
    );
  }
}

export default connect(({ system, loading }) => ({
  list: system.serverConfigList,
  loading: loading.effects["system/serverConfigList"]
}))(Params);