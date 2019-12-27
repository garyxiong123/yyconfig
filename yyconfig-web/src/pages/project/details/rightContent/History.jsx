import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table, Card, Row, Col } from 'antd';
import moment from 'moment';
import styles from '../../index.less';

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
  renderTableType(item) {
    let list = item.changeSets ? JSON.parse(item.changeSets) : {};
    let createItems = list.createItems || [], deleteItems = list.deleteItems || [], updateItems = list.updateItems.newItem || [];
    return (
      <Fragment>
        {createItems.length ? this.renderTable(createItems, '新增') : ''}
      </Fragment>
    )
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
      },
      {
        title: 'Old Value',
        dataIndex: 'oldValue',
      },
      {
        title: 'New Value',
        dataIndex: 'value',
      },
      {
        title: 'Comment',
        dataIndex: 'comment',
      },
    ];
    return (
      <Table
        columns={columns}
        dataSource={data}
        bordered
        pagination={false}
        rowKey={record => {
          return record.key;
        }}
      />
    )
  }
  render() {
    const { commitFind } = this.props;
    return (
      <Fragment>
        {
          commitFind.map((item, i) => (
            <Card key={i} className={styles.marginBottom25} title={item.updateAuthor} extra={
              item.updateTime ? moment(item.updateTime).format('YYYY-MM-DD HH:mm:ss') : ''
            } hoverable={false}>
              {
                this.renderTableType(item)
              }
            </Card>
          ))
        }
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  commitFind: project.commitFind,
  loading: loading.effects["project/commitFind"]
}))(History);
