import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table, Divider, Popconfirm, Button, Tag, message } from 'antd';
import moment from 'moment';
import ConfigAdd from '../modal/ConfigAdd';
import { project } from '@/services/project'

class TableList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showEdit: false,
      currentItem: {}
    };
  }
  componentDidMount() { }

  onEdit = (record) => {
    this.setState({
      showEdit: true,
      currentItem: record || {}
    })
  }
  onCancel = () => {
    this.setState({
      showEdit: false
    })
  }
  onDelete = async (itemId) => {
    let res = await project.configDelete({itemId});
    if(res && res.code === '1') {
      message.success('删除成功');
      this.onFetchNamespaceList();
    }
  }
  onConfigSave = () => {
    this.onFetchNamespaceList();
  }
  onFetchNamespaceList = () => {
    const { dispatch, appDetail, currentEnv } = this.props;
    let currentCluster = currentEnv.cluster || {};
    dispatch({
      type: 'project/nameSpaceList',
      payload: {
        appCode: appDetail.appCode,
        env: currentEnv.env,
        clusterName: currentCluster.name
      }
    })
  }

  renderPushStatus = (item) => {
    if (!item.deleted && !item.modified) {
      return <Tag color="#999">已发布</Tag>
    } else {
      return <Tag color="#f2aa5d">未发布</Tag>
    }
  }

  renderPushDetailsStatus = (item) => {
    if (!item.deleted && !item.modified) {
      return
    }
    if (item.deleted) {
      return <Tag color="#f00">删</Tag>
    }
    if (item.modified && !item.deleted && item.oldValue) {
      return <Tag color="#1890ff">改</Tag>
    }

    if (item.modified && !item.deleted && !item.oldValue) {
      return <Tag color="#7bd074">新</Tag>
    }
  }
  renderTable() {
    const { tableList } = this.props;
    const columns = [
      {
        title: '发布状态',
        dataIndex: 'modified',
        render: (text, record) => this.renderPushStatus(record)
      },
      {
        title: 'Key',
        dataIndex: 'item.key',
        render: (text, record) => (
          <Fragment>
            <span>{text} </span>
            {this.renderPushDetailsStatus(record)}
          </Fragment>
        )
      },
      {
        title: 'Value',
        dataIndex: 'item.value',
      },
      {
        title: '备注',
        dataIndex: 'item.comment',
      },
      {
        title: '最后修改人',
        dataIndex: 'item.updateAuthor',
      },
      {
        title: '最后修改时间',
        dataIndex: 'item.updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD') : ''}</span>
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
                修改
              </a>
              <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.item.id)}
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
        dataSource={tableList || []}
        bordered
        // onChange={this.onTableChange}
        // loading={loading}
        pagination={false}
        rowKey={record => {
          return record.item.id;
        }}
      />
    )
  }
  render() {
    const { showEdit, currentItem } = this.state;
    return (
      <Fragment>
        <Button size="small" type="primary" onClick={this.onEdit} style={{ margin: '10px 0' }}>+新增配置</Button>
        {this.renderTable()}
        {showEdit && <ConfigAdd onCancel={this.onCancel} currentItem={currentItem} onSave={this.onConfigSave} />}
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  appDetail: project.appDetail,
  currentEnv: project.currentEnv,
  // appDetail: project.appDetail,
  // loading: loading.effects["project/appList"]
}))(TableList);
