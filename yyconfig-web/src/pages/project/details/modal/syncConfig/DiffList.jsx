import React, { Fragment } from 'react';
import { project } from '@/services/project';
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
      this.setState({
        diffList: res.data || []
      })
    }
  }

  render() {
    return (
      <div>

      </div>
    )
  }
}

export default DiffList;