import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import TreeModal from './TreeModal';
import {getTree, getUserTrees, updateTree} from './Requests';
import {statuses} from '../constants';

class MyTreesModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      trees: [],
      tree: null,
      treeModal: false,
      analysisModal: false,
      error: ''
    };
  }

  componentWillMount() {
    this.loadTrees();
  }

  loadTrees = () => {
    const user = this.state.user;

    getUserTrees(user)
      .then(({data}) => this.setState({trees: data}))
      .catch(({response: {data}}) => this.setState({error: data.message}));
  }

  onToggleView = (treeId, success) => {
    if (success) {
      this.loadTrees();
    }

    if (treeId) {
      getTree(treeId)
        .then(({data}) => {
          this.setState((prevState) => ({
            tree: data,
            treeModal: !prevState.treeModal
          }));
        })
        .catch(({response: {data}}) => {
          this.setState({error: data.message});
        });
    } else {
      this.setState((prevState) => ({
        tree: null,
        treeModal: !prevState.treeModal
      }));
    }
  }

  onToggleAnalysis = (trees) => {
  }

  onCutdownTree = (treeId) => {
    getTree(treeId)
      .then(({data}) => {
        const cutTree = {
          treeId: data.treeId,
          height: data.height,
          diameter: data.diameter,
          land: data.land,
          status: statuses.cutdown.enum,
          ownership: data.ownership,
          species: data.species.name,
          municipality: data.municipality.name
        };

        const treeParams = {
          user: this.state.user,
          tree: cutTree
        };

        return treeParams;
      })
      .then((treeParams) => {
        updateTree(treeParams)
          .then(({data}) => this.setState((prevState) => ({trees: prevState.trees.map((tree) => tree.treeId === treeId ? data : tree)})))
          .catch(({response: {data}}) => this.setState({error: data.message}));
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  render() {
    return !this.state.treeModal ? (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='tree' circular/>
              <Header.Content>My Trees</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid textAlign='center' verticalAlign='middle' columns={8}>
              <Grid.Column>
                <Header.Content as='h4'>Tree ID</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Height<br/>(cm)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Diameter<br/>(cm)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Species</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Status</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Municipality</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Date Planted</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Actions</Header.Content>
              </Grid.Column>
            </Grid>

            <Divider/>

            {!this.state.error && this.state.trees.length !== 0 ? (
              <Grid textAlign='center' verticalAlign='middle' columns={8}>
                {this.state.trees.map(({treeId, height, diameter, species, status, municipality, datePlanted}) => (
                  <Grid.Row key={treeId}>
                    <Grid.Column>{treeId}</Grid.Column>
                    <Grid.Column>{height}</Grid.Column>
                    <Grid.Column>{diameter}</Grid.Column>
                    <Grid.Column>{species.name}</Grid.Column>
                    <Grid.Column>{status}</Grid.Column>
                    <Grid.Column>{municipality.name}</Grid.Column>
                    <Grid.Column>{datePlanted}</Grid.Column>
                    <Grid.Column>
                      <Button inverted circular size='mini' content='View' color='green' disabled={!this.state.user} onClick={() => this.onToggleView(treeId, false)}/>
                      <Button inverted circular size='mini' icon='cut' color='red' disabled={!this.state.user} onClick={() => this.onCutdownTree(treeId)}/>
                    </Grid.Column>
                  </Grid.Row>
                ))}
              </Grid>
            ) : this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : (
              <Message info>
                <Message.Header style={{textAlign: 'center'}}>Looks like you haven't planted any trees yet!</Message.Header>
              </Message>
            )}

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='blue' size='small' onClick={() => this.onToggleAnalysis(this.state.trees)}>Analysis</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    ) : (
      <TreeModal tree={this.state.tree} onClose={this.onToggleView}/>
    );
  }
}

MyTreesModal.propTypes = {
  onClose: PropTypes.func.isRequired
};

export default MyTreesModal;
