import React, { Fragment } from 'react';
import { Card, Table, Button, Input, Divider, Popconfirm, Select, Row, Col, message } from 'antd';
import styles from './index.less';
import { DepartmentModal } from './department/index';
import { connect } from 'dva';
import moment from 'moment';
import { department } from '@/services/auth';

const { Option } = Select;

class Department extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
      showEditModal: false,
      currentItem: {}
    };
  }
  // ----------------------------------生命周期----------------------------------------
  componentDidMount() {
    this.onFetchList()
  }
  // ----------------------------------事件----------------------------------------
  onFetchList = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'auth/departmentList',
      payload: searchObj
    })
  }
  onSearch = (value) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        condition: value
      }
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
  onDelete = async (departmentId) => {
    let res = await department.departmentDelete({ departmentId });
    if (res && res.code === '1') {
      message.success('刪除成功');
      this.onFetchList();
    }
  }
  onSave = () => {
    this.onFetchList();
  }
  // ----------------------------------View----------------------------------------
  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: '部门名称',
        dataIndex: 'name',
      },
      {
        title: 'code',
        dataIndex: 'code',
      },
      {
        title: '备注',
        dataIndex: 'comment'
      },
      {
        title: '创建人',
        dataIndex: 'createAuthor',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
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
  renderQuery() {
    // const { partTypeList } = this.state;
    return (
      <div>
        <Input.Search onSearch={this.onSearch} placeholder="请输入机构名称" />
      </div>
      // <Row type="flex" gutter={48}>
      //   <Col>
      //     <Input.Search onSearch={this.onSearch} placeholder="请输入机构名称" />
      //   </Col>
      //   <Col>
      //     <Select placeholder="请选择机构类型" style={{width: 200}}>
      //       {
      //         partTypeList && partTypeList.map((item, i) => (
      //           <Option>{item.name}</Option>
      //         ))
      //       }
      //     </Select>
      //   </Col>
      // </Row>
    )
  }
  render() {
    const { showEditModal, currentItem } = this.state;
    return (
      <Card title={
        <Button type="primary" onClick={() => { this.onEdit() }}> + 添加机构</Button>
      } extra={this.renderQuery()}>
        {
          this.renderTable()
        }
        {showEditModal && <DepartmentModal onCancel={this.onCancel} currentItem={currentItem} onSave={this.onSave} />}
      </Card>
    );
  }
}

export default connect(({ auth, loading }) => ({
  list: auth.departmentList,
  loading: loading.effects["auth/departmentList"]
}))(Department);
