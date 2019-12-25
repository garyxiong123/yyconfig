import React from 'react';
import { connect } from 'dva';
import { Input } from 'antd';

class SearchApp extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {
        page: 1,
        size: 10
      }
    };
  }

  componentDidMount() {
  }
  
  componentDidUpdate(prevProps, prevState) {
    const { searchObj } = this.state;
    if (prevState.searchObj !== searchObj) {
      this.onFetchAppList()
    }
  }

  onFetchAppList = () => {
    const { dispatch } = this.props;
    const { searchObj } = this.state;
    dispatch({
      type: 'project/appList',
      payload: searchObj
    })
  }

  onSearch = (value) => {
    const { searchObj } = this.state;
    this.setState({
      searchObj: {
        ...searchObj,
        query: value
      }
    })
  }
  render() {
    return (
      <Input.Search placeholder="搜索项目" onSearch={this.onSearch} />
    )
  }

}

export default connect(({ }) => ({

}))(SearchApp);
