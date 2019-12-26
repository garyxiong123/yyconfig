import React, { Fragment } from 'react';
import { connect } from 'dva';
import { Card, Row, Col, Icon, Button, Table } from 'antd';
import router from 'umi/router';
import styles from '../index.less';
import CreateProject from '../create/';
import Loading from '@/pages/components/loading/'

class MyProject extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showCreate: false,
      searchObj: {
        page: 1,
        size: 10
      }
    };
  }
  // ----------------------------------生命周期-----------------------------
  componentDidMount() {
    const { list } = this.props;
    const { searchObj } = this.state;
    this.onFetchlist();

  }
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (prevState.searchObj !== searchObj) {
      this.onFetchlist()
    }
  }
  // ----------------------------------事件-----------------------------
  onRouteTo = (pathname, data) => {
    router.push({
      pathname,
      query: data
    })
  }
  onFetchlist = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'project/appList',
      payload: searchObj
    })
  }
  onShowCreate = () => {
    this.setState({
      showCreate: true
    })
  }
  onCancel = () => {
    this.setState({
      showCreate: false
    })
  }
  onSave = () => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        page: 1
      }
    })
  }
  onQueryMore = () => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        page: searchObj.page + 1
      }
    })
  }
  // ----------------------------------渲染-----------------------------
  render() {
    const { list, loading } = this.props;
    const { showCreate } = this.state;
    return (
      <Fragment>
        <Row gutter={48} type="flex">
          <Col lg={6} md={8} sm={24}>
            <Button type="dashed" className={styles.listCard} onClick={this.onShowCreate}>
              <Icon type="plus" />
              <span>新增项目</span>
            </Button>
          </Col>
          {
            loading ? <Loading />
              :
              <Fragment>
                {
                  list.rows && list.rows.map((item, i) => (
                    <Col lg={6} md={8} sm={24} key={i}>
                      <Card className={styles.listCard} onClick={() => { this.onRouteTo(`/project/details/${item.id}`, { appId: item.id, appCode: item.appCode }) }}>
                        <h2>{item.appCode}</h2>
                        <p>{item.name}</p>
                      </Card>
                    </Col>
                  ))
                }
              </Fragment>
          }
        </Row>
        {
          list.totalPage > list.pageNum &&
          <div className={styles.textCenter}>
            <Button type="link" onClick={this.onQueryMore}>加载更多<Icon type="down" style={{ fontSize: 13 }} /></Button>
          </div>
        }
        {
          showCreate && <CreateProject onCancel={this.onCancel} onSave={this.onSave} />
        }
      </Fragment>
    );
  }
}

export default connect(({ project, loading }) => ({
  list: project.appList,
  loading: loading.effects["project/appList"]
}))(MyProject);
