import React, { Fragment } from 'react';
import { Card, Table, Button, Input, Divider, Popconfirm } from 'antd';
import moment from 'moment';
import styles from './index.less';
import { UserEditModal } from './user/index';

class User extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
      list: [{}, {}, {}],
      showEditModal: false,
      currentUser: {}
    };
  }
  // ----------------------------------生命周期----------------------------------------
  componentDidMount() { }

  // ----------------------------------事件----------------------------------------
  onSearch = (value) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        condition: value
      }
    })
  }
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const { searchObj } = this.state;
    // const { list } = this.props;
    // let sort = order ? {
    //   sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
    //   sortName: field ? field : ''
    // } : {};
    // let params = {
    //   ...searchObj,
    //   ...sort,
    //   pageNo: pageSize === list.pageSize ? parseInt(current) : 1,
    //   pageSize: parseInt(pageSize),
    // };
    // this.setState({
    //   searchObj: params
    // });
  };
  onAdd = (record = {}) => {
    this.setState({
      showEditModal: true,
      currentUser: record
    })
  }
  onDelete = (userId) => {
    console.log('onDelete-->', userId)
  }
  onCancel = () => {
    this.setState({
      showEditModal: false
    })
  }
  // ----------------------------------View----------------------------------------
  renderTable() {
    const { list } = this.state;
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
        title: '邮箱',
        dataIndex: 'email'
      },
      {
        title: '角色',
        dataIndex: 'userRoles',
        className: styles.textOver,
        width: 200,
        // render: (text, record) => (
        //   <Fragment>
        //     {
        //       text.map((item, i) => (
        //         <span key={i}>{item.roleName}{i < text.length - 1 ? '，' : ""}</span>
        //       ))
        //     }
        //   </Fragment>
        // )
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
                onConfirm={() => this.onDelete(record.userId)}
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
        onChange={this.onTableChange}
        pagination={false}
        // loading={loading}
        // pagination={{
        //   pageSizeOptions: ['10', '20', '30', '50'],
        //   total: list.totalCount || 0,
        //   showTotal: (total, range) =>
        //     `共${list.totalCount || 0}条，当前${list.pageNum ? list.pageNum : 1}/${
        //     list.totalPage ? list.totalPage : 1
        //     }页`,
        //   showSizeChanger: true,
        //   current: list.pageNum ? list.pageNum : 1,
        //   pageSize: list.pageSize ? list.pageSize : 10,
        // }}
        rowKey={record => {
          return record.userId;
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
        {showEditModal && <UserEditModal visible={showEditModal} onCancel={this.onCancel} currentUser={currentUser} />}
      </Card>
    );
  }
}
export default User;
