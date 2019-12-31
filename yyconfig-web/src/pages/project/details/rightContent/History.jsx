import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table, Card, Row, Col, Empty, Input } from 'antd';
import moment from 'moment';
import styles from '../../index.less';

const { TextArea } = Input;

class History extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      list: [{}, {}]
    };
  }
  componentDidMount() {
    this.onFetchHistoryList();
  }

  onFetchHistoryList = () => {
    const { item, dispatch } = this.props;
    let baseInfo = item.baseInfo || {};
    dispatch({
      type: 'project/commitFind',
      payload: {
        appEnvClusterNamespaceId: baseInfo.id
      }
    })

  }
  renderTable(data, type) {
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
        dataIndex: 'key',
        width: '25%'
      },
      {
        title: 'Old Value',
        dataIndex: 'oldValue',
        width: '22%'
      },
      {
        title: 'New Value',
        dataIndex: 'value',
        width: '22%'
      },
      {
        title: 'Comment',
        dataIndex: 'comment',
        width: '20%'
      },
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
  renderText(item) {
    let value = item[0] ? item[0].value : '';
    return (
      <TextArea rows={6} value={item[0].value} readOnly />
    )
  }

  renderItem(item, i, currentItem) {
    let list = item.changeSets ? JSON.parse(item.changeSets) : {};
    let createItems = list.createItems || [], deleteItems = list.deleteItems || [], updateItems = list.updateItems || [];
    return (
      <Fragment key={i}>
        {
          createItems.length ?
            <Card key={i} className={styles.marginBottom25} title={item.updateAuthor} extra={
              item.updateTime ? moment(item.updateTime).format('YYYY-MM-DD HH:mm:ss') : ''
            } hoverable={false} size="small" bordered={false}>
              {
                currentItem.format === 'Properties' ?
                  this.renderTable(createItems, '新增') :
                  this.renderText(createItems)
              }
            </Card> : null
        }
        {
          deleteItems.length ?
            <Card key={i} className={styles.marginBottom25} title={item.updateAuthor} extra={
              item.updateTime ? moment(item.updateTime).format('YYYY-MM-DD HH:mm:ss') : ''
            } hoverable={false} size="small" bordered={false}>
              {
                this.renderTable(deleteItems, '删除')
              }
            </Card> : null
        }
        {
          updateItems.length ?
            <Card key={i} className={styles.marginBottom25} title={item.updateAuthor} extra={
              item.updateTime ? moment(item.updateTime).format('YYYY-MM-DD HH:mm:ss') : ''
            } hoverable={false} size="small" bordered={false}>
              {
                this.renderTableUpdate(updateItems, '更新')
              }
            </Card> : null
        }
      </Fragment>
    )
  }
  render() {
    const { commitFind, item } = this.props;
    let baseInfo = item.baseInfo || {};
    return (
      <Fragment>
        {
          baseInfo.id && commitFind[baseInfo.id] && commitFind[baseInfo.id].length ?
            <Fragment>
              {
                commitFind[baseInfo.id].map((vo, i) => this.renderItem(vo, i, item))
              }
            </Fragment> :
            <Empty />
        }
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  commitFind: project.commitFind,
  loading: loading.effects["project/commitFind"]
}))(History);
