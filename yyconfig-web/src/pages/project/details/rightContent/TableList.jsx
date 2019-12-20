import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table, Divider, Popconfirm } from 'antd';
import moment from 'moment';

class TableList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }
  componentDidMount() { }

  renderTable() {
    const { tableList } = this.props;
    const columns = [
      {
        title: '发布状态',
        dataIndex: 'a',
      },
      {
        title: 'Key',
        dataIndex: 'item.key',
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
              // onClick={() => {
              //   this.onAdd(record);
              // }}
              >
                修改
              </a>
              <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                // onConfirm={() => this.onDelete(record.id)}
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
          return record.id;
        }}
      />
    )
  }
  render() {
    return (
      <Fragment>
        {this.renderTable()}
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  // appDetail: project.appDetail,
  // loading: loading.effects["project/appList"]
}))(TableList);
