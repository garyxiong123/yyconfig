import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Table, Divider, Popconfirm, Button, Tag, message, Row, Col, Card, Input } from 'antd';
import moment from 'moment';
import ConfigAdd from '../modal/ConfigAdd';
import { project } from '@/services/project';
import styles from '../../index.less';

class TableList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showEdit: false,
      currentItem: {},
      copyValue: ''
    };
  }
  componentDidMount() {
    const { item } = this.props;
    if (item.namespaceType === 'Associate') {
      this.onFetchAssociatedPublicNamespace();
    }
  }
  componentDidUpdate(prevProps, prevState) {
    const { copyValue } = this.state;
    if (prevState.copyValue !== copyValue && !prevState.copyValue && copyValue) {
      this.onCopy()
    }
  }

  onFetchAssociatedPublicNamespace = () => {
    const { currentEnv, item, dispatch } = this.props;
    let baseInfo = item.baseInfo || {};
    dispatch({
      type: 'project/associatedPublicNamespace',
      payload: {
        env: currentEnv.env,
        appCode: baseInfo.appCode,
        namespaceName: baseInfo.namespaceName,
        clusterName: baseInfo.clusterName
      }
    })
  }
  //关联数据覆盖配置
  onRecover = (record) => {

  }
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
    let res = await project.configDelete({ itemId });
    if (res && res.code === '1') {
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
  onDoubleClick = (text) => {
    this.setState({
      copyValue: text
    })
  }
  onCopy = () => {
    const { item } = this.props;
    let baseInfo = item.baseInfo || {};
    let text = document.getElementById('copy'+baseInfo.id);
    try {
      text.select();
      document.execCommand('copy')
      message.success('复制成功');
      this.setState({
        copyValue: ''
      })
    } catch (error) {

    }
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
  renderTable(tableList, type) {
    // const { tableList } = this.props;
    const columns = [
      {
        title: '发布状态',
        dataIndex: 'modified',
        className: type === 'Associate' ? styles.hidden : '',
        render: (text, record) => this.renderPushStatus(record)
      },
      {
        title: 'Key',
        dataIndex: 'item.key',
        width: '20%',
        render: (text, record) => (
          <div onDoubleClick={() => this.onDoubleClick(text)}>
            <span>{text} </span>
            {this.renderPushDetailsStatus(record)}
          </div>
        )
      },
      {
        title: 'Value',
        dataIndex: 'item.value',
        width: '20%',
        render: (text, record) => (
          <div onDoubleClick={() => this.onDoubleClick(text)}>
            <span title={text}>{text}</span>
          </div>
        )
      },
      {
        title: '备注',
        dataIndex: 'item.comment',
        width: 100
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
        dataIndex: 'deleted',
        render: (text, record) => (
          <Fragment>
            {
              type && type === 'Associate' ?
                <a onClick={() => { this.onRecover(record) }}>覆盖</a> :
                <Fragment>
                  {
                    !text &&
                    <span>
                      <a onClick={() => { this.onEdit(record) }}>修改</a>
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
                  }
                </Fragment>
            }

          </Fragment>
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
  renderOpe(item) {
    return (
      <Row type="flex" justify="end" gutter={16} style={{ marginBottom: 15 }}>
        <Col>
          <Button size="small">
            同步配置
          </Button>
        </Col>
        {
          item.namespaceType !== 'Associate' &&
          <Col>
            <Button size="small" type="primary" onClick={this.onEdit}>+新增配置</Button>
          </Col>
        }
      </Row>
    )
  }
  renderAssociateList() {
    const { item, tableList, associatedPublicNamespace } = this.props;
    console.log('associatedPublicNamespace-->', associatedPublicNamespace)
    let baseInfo = item.baseInfo || {};
    let listItem = associatedPublicNamespace[baseInfo.id] || {};
    return (
      <Fragment>
        <Card
          title="覆盖的配置"
          bordered={false}
        >
          {this.renderTable(tableList)}
        </Card>
        <Card
          title="公共的配置"
          bordered={false}
        >
          {this.renderTable(listItem.items, 'Associate')}
        </Card>
      </Fragment>
    )
  }
  render() {
    const { showEdit, currentItem, copyValue } = this.state;
    const { item, tableList } = this.props;
    let baseInfo = item.baseInfo || {};
    return (
      <Fragment>
        {this.renderOpe(item)}
        {item.namespaceType === 'Associate' ? this.renderAssociateList() : this.renderTable(tableList)}
        {showEdit && <ConfigAdd onCancel={this.onCancel} currentItem={currentItem} onSave={this.onConfigSave} baseInfo={item.baseInfo} />}
        <Input value={copyValue} id={'copy'+baseInfo.id} className={styles.copyInput}/>
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  appDetail: project.appDetail,
  currentEnv: project.currentEnv,
  associatedPublicNamespace: project.associatedPublicNamespace
  // appDetail: project.appDetail,
  // loading: loading.effects["project/appList"]
}))(TableList);
