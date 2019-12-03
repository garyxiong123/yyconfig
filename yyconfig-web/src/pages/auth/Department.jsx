import React, { Fragment } from 'react';
import { Card, Table, Button, Input, Divider, Popconfirm, Select, Row, Col } from 'antd';
import styles from './index.less';

const { Option } = Select;

class Department extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
      list: [{}, {}, {}],
      partTypeList: []
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

  // ----------------------------------View----------------------------------------
  renderTable() {
    const { list } = this.state;
    const columns = [
      {
        title: '部门名称',
        dataIndex: 'name',
      },
      {
        title: '部门类型',
        dataIndex: 'type',
      },
      {
        title: '备注',
        dataIndex: 'email'
      },
      {
        title: '创建人',
        dataIndex: 'updateAuthor',
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
        onChange={this.onTableChange}
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
          return record.id;
        }}
      />
    )
  }
  renderQuery() {
    const { partTypeList } = this.state;
    return (
      <Row type="flex" gutter={48}>
        <Col>
          <Input.Search onSearch={this.onSearch} placeholder="请输入机构名称" />
        </Col>
        <Col>
          <Select placeholder="请选择机构类型" style={{width: 200}}>
            {
              partTypeList && partTypeList.map((item, i) => (
                <Option>{item.name}</Option>
              ))
            }
          </Select>
        </Col>
      </Row>
    )
  }
  render() {
    return (
      <Card title={
        <Button type="primary"> + 添加机构</Button>
      } extra={this.renderQuery()}>
        {
          this.renderTable()
        }
      </Card>
    );
  }
}
export default Department;
