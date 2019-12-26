import React from 'react';
import { connect } from 'dva';
import { Input, Select } from 'antd';
import router from 'umi/router';
const { Option } = Select;
class SearchApp extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: ''
    };
  }

  componentDidMount() {
    this.onFetchAppListAll();
  }

  componentDidUpdate(prevProps, prevState) {
    const { } = this.props;
  }

  onFetchAppListAll = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/appListAll',
      payload: {}
    })
  }

  onSearch = (value, option) => {
    let item = option.props.label;
    // this.setState({
    //   value
    // })
    router.replace({
      pathname: `/project/details/${item.id}`,
      query: {
        appId: item.id,
        appCode: item.appCode
      }
    })
    // const { searchObj } = this.state;
    // this.setState({
    //   searchObj: {
    //     ...searchObj,
    //     query: value
    //   }
    // })
  }
  render() {
    const { appListAll } = this.props;
    const { value } = this.state;
    return (
      <Select
        placeholder="搜索项目"
        showSearch
        style={{ width: 300 }}
        optionFilterProp="children"
        showArrow={false}
        onChange={this.onSearch}
        value={undefined}
      >
        {
          appListAll.map((item) => (
            <Option value={item.name} key={item.id} label={item}>{item.appCode}/{item.name}</Option>
          ))
        }
      </Select>
    )
  }

}

export default connect(({ project }) => ({
  appListAll: project.appListAll,
}))(SearchApp);
