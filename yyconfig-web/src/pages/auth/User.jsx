import React, { Fragment } from 'react';
import { Card, Table, Button, Input, Divider, Popconfirm, message } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import styles from './index.less';
import { UserEditModal } from './user/index';
import { auth } from '@/services/auth';

class User extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
      showEditModal: false,
      currentUser: {}
    };
  }
  // ----------------------------------生命周期----------------------------------------
  componentDidMount() {
    this.onFetchList()
  }
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (prevState.searchObj !== searchObj) {
      this.onFetchList();
    }
  }
  // ----------------------------------事件----------------------------------------
  onFetchList = () => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'auth/userList',
      payload: searchObj
    })
  }
  onSearch = (value) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        pageNo: 1,
        condition: value
      }
    })
  }
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const { searchObj } = this.state;
    const { list } = this.props;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...searchObj,
      ...sort,
      pageNo: pageSize === list.pageSize ? parseInt(current) : 1,
      pageSize: parseInt(pageSize),
    };
    this.setState({
      searchObj: params
    });
  };
  onAdd = (record = {}) => {
    this.setState({
      showEditModal: true,
      currentUser: record
    })
  }
  onDelete = async (userId) => {
    let res = await auth.userDelete({ userId });
    if (res && res.code == '1') {
      message.success('删除成功');
      this.onFetchList();
    }
  }
  onCancel = () => {
    this.setState({
      showEditModal: false
    })
  }
  onSave = () => {
    this.onFetchList();
  }
  // ----------------------------------View----------------------------------------
  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: '用户名',
        dataIndex: 'username',
      },
      {
        title: '全名',
        dataIndex: 'realName',
      },
      {
        title: '部门',
        dataIndex: 'department.name',
      },
      {
        title: '邮箱',
        dataIndex: 'email'
      },
      {
        title: '角色',
        dataIndex: 'roles',
        // className: styles.textOver,
        width: 200,
        render: (text, record) => (
          <Fragment>
            {
              text.length ?
                <Fragment>
                  {
                    text.map((item, i) => (
                      <span key={i}>{item.roleName}{i < text.length - 1 ? '，' : ""}</span>
                    ))
                  }
                </Fragment> :
                <span>普通用户</span>
            }
          </Fragment>
        )
      },
      {
        title: '修改人',
        dataIndex: 'updateAuthor',
      },
      {
        title: '修改时间',
        dataIndex: 'updateTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
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
                  this.onAdd(record);
                }}
              >
                修改
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
        dataSource={list.rows || []}
        onChange={this.onTableChange}
        loading={loading}
        pagination={{
          pageSizeOptions: ['10', '20', '30', '50'],
          total: list.totalCount || 0,
          showSizeChanger: true,
          current: list.pageNum ? list.pageNum : 1,
          pageSize: list.pageSize ? list.pageSize : 10,
        }}
        rowKey={record => {
          return record.id;
        }}
      />
    )
  }
  render() {
    const { showEditModal, currentUser } = this.state;
    return (
      <Card title={
        <Button type="primary" onClick={() => { this.onAdd() }}> + 新增用户</Button>
      } extra={
        <Input.Search onSearch={this.onSearch} placeholder="搜索用户" />
      }>
        {
          this.renderTable()
        }
        {showEditModal && <UserEditModal onCancel={this.onCancel} currentUser={currentUser} onSave={this.onSave} />}
      </Card>
    );
  }
}
export default connect(({ auth, loading }) => ({
  list: auth.userList,
  loading: loading.effects["auth/userList"]
}))(User);
