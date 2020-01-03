import React, { Fragment } from 'react';
import { project } from '@/services/project';
import { Table, Card, Row, Col, Empty, Tag } from 'antd';
import styles from '../../../index.less';

class DiffList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      diffList: []
    };
  }

  componentDidMount() {
    this.onFetchSyncConfigDiff();
  }

  onFetchSyncConfigDiff = async () => {
    const { syncItems, syncToNamespaces } = this.props;
    let res = await project.syncConfigDiff({
      syncItems,
      syncToNamespaces
    })
    if (res && res.code === '1') {
      this.onGetList(res.data || [])
      // this.setState({
      //   diffList: res.data || []
      // })
    }
  }
  onGetList = (list) => {
    let newList = [];
    list.map((voItem) => {
      let vo = voItem.diffs || {}, newCreateItems = [], newUpdateItems = [], newDeleteItems = [];
      vo.createItems && vo.createItems.map((item) => {
        newCreateItems.push({
          ...item,
          type: '新增'
        })
      })
      vo.deleteItems && vo.deleteItems.map((item) => {
        newDeleteItems.push({
          ...item,
          type: '删除'
        })
      })
      vo.updateItems && vo.updateItems.map((item) => {
        newUpdateItems.push({
          key: item.newItem.key,
          oldValue: item.oldItem.value,
          value: item.newItem.value,
          Comment: item.newItem.comment,
          type: '更新'
        })
      })
      newList.push({
        namespace: voItem.namespace,
        items: [...newCreateItems, ...newUpdateItems, ...newDeleteItems]
      })
      this.setState({
        diffList: newList
      })
    })
  }
  onDelete = (key) => {
    console.log('onDelete-->', key)
  }
  renderTable(data, type) {
    const columns = [
      {
        title: 'Type',
        dataIndex: 'type',
        // render: () => (
        //   <span>{type}</span>
        // )
      },
      {
        title: 'Key',
        dataIndex: 'key',
        width: '25%'
      },
      {
        title: '同步前',
        dataIndex: 'oldValue',
        width: '22%'
      },
      {
        title: '同步后',
        dataIndex: 'value',
        width: '22%'
      },
      {
        title: 'Comment',
        dataIndex: 'comment',
      },
      // {
      //   title: '操作',
      //   dataIndex: 'ope',
      //   width: 80,
      //   render: (text, record) => (
      //     <Popconfirm
      //       title="确定删除吗?"
      //       onConfirm={() => this.onDelete(record)}
      //       okText="确定"
      //       cancelText="取消"
      //     >
      //       <a>删除</a>
      //     </Popconfirm>
      //   )
      // },
    ];
    return (
      <Table
        columns={columns}
        dataSource={data}
        bordered
        size="small"
        pagination={false}
        rowKey={record => {
          return record.key;
        }}
      />
    )
  }
  renderTableUpdate(data, type) {
    const columns = [
      {
        title: 'Type',
        dataIndex: 'type',
        render: () => (
          <span>{type}</span>
        )
      },
      {
        title: 'Key',
        dataIndex: 'newItem.key',
        width: '25%'
      },
      {
        title: 'Old Value',
        dataIndex: 'oldItem.value',
        width: '22%'
      },
      {
        title: 'New Value',
        dataIndex: 'newItem.value',
        width: '22%'
      },
      {
        title: 'Comment',
        dataIndex: 'newItem.comment',
        width: '20%'
      },
      // {
      //   title: '操作',
      //   dataIndex: 'ope',
      //   width: 80,
      //   render: (text, record) => (
      //     <Popconfirm
      //       title="确定删除吗?"
      //       onConfirm={() => this.onDelete(record)}
      //       okText="确定"
      //       cancelText="取消"
      //     >
      //       <a>删除</a>
      //     </Popconfirm>
      //   )
      // },
    ];
    return (
      <Table
        columns={columns}
        dataSource={data}
        size="small"
        bordered
        pagination={false}
        rowKey={record => {
          return record.newItem.key;
        }}
      />
    )
  }
  renderItem(item, i) {
    let namespace = item.namespace || {}, list = item.items || [];
    // let namespace = item.namespace || {}, list = item.diffs || {};
    // let createItems = list.createItems || [], deleteItems = list.deleteItems || [], updateItems = list.updateItems || [];
    return (
      <Fragment key={i}>
        <Card className={styles.marginBottom25} bordered={false} title={
          <Fragment>
            <Tag color="#2db7f5">{`环境：${namespace.env}`}</Tag>
            <Tag color="#87d068">{`集群：${namespace.clusterName}`}</Tag>
            <Tag color="#108ee9">{`Namespace：${namespace.namespaceName}`}</Tag>
          </Fragment>
        }>
          {this.renderTable(list)}
        </Card>
        {/* <Card className={styles.marginBottom25} title={`环境：${namespace.env} -- 集群：${namespace.clusterName} -- Namespace：${namespace.namespaceName}`} hoverable={false} size="small" bordered={false}>
          {
            this.renderTable(deleteItems, '删除')
          }
        </Card>
        <Card className={styles.marginBottom25} title={item.updateAuthor} title={`环境：${namespace.env} -- 集群：${namespace.clusterName} -- Namespace：${namespace.namespaceName}`} hoverable={false} size="small" bordered={false}>
          {
            this.renderTableUpdate(updateItems, '更新')
          }
        </Card> */}
      </Fragment>
    )
  }
  render() {
    const { diffList } = this.state;
    return (
      <Fragment>
        {
          diffList.length > 0 ?
            diffList.map((item, i) => (
              this.renderItem(item, i)
            )) : null
        }
      </Fragment>
    )
  }
}

export default DiffList;