import React, { Fragment } from 'react';
import { Card, Table, Radio, Icon, Button } from 'antd';
import { instances } from '@/services/project';
import moment from 'moment';
import styles from '../../index.less';

class Case extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      releaseId: '',
      type: '1',
      newRelease: {},
      notNewRelease: {},
      allRelease: {},
      newReleaseParams: {
        pageNo: 0
      },
      notNewReleaseParams: {
        pageNo: 0
      },
      allReleaseParams: {
        pageNo: 0
      },
    };
  }
  componentDidMount() {
    this.onFetchRelease();
  }
  componentDidUpdate(prevProps, prevState) {
    const { releaseId } = this.state;
    if (releaseId && prevState.releaseId !== releaseId) {
      this.onFetchNewRelease()
    }
  }

  onFetchRelease = async () => {
    const { item } = this.props;
    let baseInfo = item.baseInfo || {};
    let res = await instances.activeRelease({
      namespaceId: baseInfo.id,
      page: 0,
      size: 1
    });
    if (res && res.code === '1' && res.data) {
      let data = res.data || [];
      this.setState({
        releaseId: (data[0] || {}).id
      })
    }
  }
  onFetchNewRelease = async () => {
    const { releaseId, newReleaseParams, newRelease } = this.state;
    if (releaseId) {
      let res = await instances.getByRelease({
        data: releaseId,
        pageNo: newReleaseParams.pageNo,
        pageSize: 20
      })
      let data = res.data || {};
      this.setState({
        newRelease: {
          ...data,
          content: [
            ...(newRelease.content || []),
            ...(data.content || []),
          ]
        }
      })
    }
  }
  onFetchNotNewRelease = async () => {
    const { item } = this.props;
    const { releaseId, notNewReleaseParams, notNewRelease } = this.state;
    let baseInfo = item.baseInfo || {};
    if (releaseId) {
      let res = await instances.getByReleaseNotIn({
        releaseIds: releaseId,
        namespaceId: baseInfo.id,
        pageNo: notNewReleaseParams.pageNo,
        pageSize: 20
      })
      let data = res.data || {};
      this.setState({
        notNewRelease: {
          ...data,
          content: [
            ...(data.content || []),
            ...(notNewRelease.content || []),
          ]
        }
      })
    }
  }
  onFetchAllRelease = async () => {
    const { item } = this.props;
    const { releaseId, allReleaseParams, allRelease } = this.state;
    let baseInfo = item.baseInfo || {};
    if (releaseId) {
      let res = await instances.getAllRelease({
        data: {
          releaseId,
          namespaceId: baseInfo.id
        },
        pageNo: allReleaseParams.pageNo,
        pageSize: 20
      })
      let data = res.data || {};
      this.setState({
        allRelease: {
          ...data,
          content: [
            ...(data.content || []),
            ...(allRelease.content || []),
          ]
        }
      })
    }
  }
  onTypeChange = (e) => {
    const { newRelease, notNewRelease, allRelease } = this.state;
    this.setState({
      type: e.target.value
    })
    switch (e.target.value) {
      case '1': {
        if (!newRelease.content) {
          this.onFetchNewRelease()
        }
      } break;
      case '2': {
        if (!notNewRelease.content) {
          this.onFetchNotNewRelease()
        }
      } break;
      case '3': {
        if (!allRelease.content) {
          this.onFetchAllRelease()
        }
      } break;
    }
  }
  onRefresh = () => {
    this.onFetchRelease();
    this.setState({
      type: '1',
      newRelease: {},
      notNewRelease: {},
      allRelease: {},
      newReleaseParams: {
        pageNo: 0
      },
      notNewReleaseParams: {
        pageNo: 0
      },
      allReleaseParams: {
        pageNo: 0
      },
    },()=>{
      this.onFetchNewRelease();
    })
  }
  getReleaseKey = () => {
    const { type, newRelease } = this.state;
    if (type !== '1') {
      return false
    }
    let currentItem = newRelease.content ? newRelease.content[0] : {};
    let configs = currentItem && currentItem.configs ? currentItem.configs[0] : {};
    let key = configs.release && configs.release.releaseKey;
    if (key) {
      return key
    } else {
      return false
    }
  }
  //加载更多
  onMore = () => {
    const { type, newReleaseParams, notNewReleaseParams, allReleaseParams } = this.state;
    switch (type) {
      case '1': {
        this.setState({
          newReleaseParams: {
            pageNo: newReleaseParams.pageNo + 1
          }
        }, () => {
          this.onFetchNewRelease()
        })
      } break;
      case '2': {
        this.setState({
          notNewReleaseParams: {
            pageNo: notNewReleaseParams.pageNo + 1
          }
        }, () => {
          this.onFetchNotNewRelease()
        })
      } break;
      case '3': {
        this.setState({
          allReleaseParams: {
            pageNo: allReleaseParams.pageNo + 1
          }
        }, () => {
          this.onFetchAllRelease()
        })
      } break;
    }

  }
  renderTable(list) {
    const { type } = this.state;
    let configs = list.configs ? list.configs[0] : {};
    const columns = [
      {
        title: 'App ID',
        dataIndex: 'appId',
      },
      {
        title: 'Cluster Name',
        dataIndex: 'clusterName',
      },
      {
        title: 'Data Center',
        dataIndex: 'dataCenter',
      },
      {
        title: 'IP',
        dataIndex: 'ip',
      },
      {
        title: '配置获取时间',
        dataIndex: 'configs.dataChangeLastModifiedTime',
        className: type !== '1' ? styles.hidden : '',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
        )
      },
    ];
    return (
      <Fragment>
        <Table
          columns={columns}
          dataSource={list.content || []}
          bordered
          title={this.getReleaseKey() ? this.getReleaseKey : false}
          pagination={false}
          rowKey={record => {
            return record.id;
          }}
        />

      </Fragment>
    )
  }
  renderExtra() {
    const { type } = this.state;
    return (
      <Fragment>
        <Radio.Group value={type} buttonStyle="solid" size="small" onChange={this.onTypeChange}>
          <Radio.Button value="1">使用最新配置的实例</Radio.Button>
          <Radio.Button value="2">使用非最新配置的实例</Radio.Button>
          <Radio.Button value="3">所有实例</Radio.Button>
        </Radio.Group>
        <Button style={{ marginLeft: 10 }} size="small" onClick={this.onRefresh}><Icon type="redo" /></Button>
      </Fragment>

    )
  }
  render() {
    const { newRelease, notNewRelease, allRelease, type } = this.state;
    return (
      <Fragment>
        <Card
          title={
            <span style={{ fontSize: 14 }}>实例说明:只展示最近一天访问过Apollo的实例</span>
          }
          bordered={false}
          size="samll"
          extra={this.renderExtra()}
          headStyle={{ backgroundColor: '#f5f5f5' }}
        >
          {
            type === '1' &&
            <Fragment>
              {this.renderTable(newRelease)}
              {
                (newRelease.content || {}).length < newRelease.total &&
                <div style={{ paddingTop: 15, textAlign: 'center' }}>
                  <a onClick={this.onMore}>加载更多</a>
                </div>
              }
            </Fragment>

          }
          {
            type === '2' &&
            <Fragment>
              {this.renderTable(notNewRelease)}
              {
                (notNewRelease.content || {}).length < notNewRelease.total &&
                <div style={{ paddingTop: 15, textAlign: 'center' }}>
                  <a onClick={this.onMore}>加载更多</a>
                </div>
              }
            </Fragment>

          }
          {
            type === '3' &&
            <Fragment>
              {this.renderTable(allRelease)}
              {
                (allRelease.content || {}).length < allRelease.total &&
                <div style={{ paddingTop: 15, textAlign: 'center' }}>
                  <a onClick={this.onMore}>加载更多</a>
                </div>
              }
            </Fragment>
          }
        </Card>
      </Fragment>
    );
  }
}
export default Case;
