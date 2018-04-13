import React, {PureComponent} from 'react';
import {Button, Divider, Header, Icon, Grid, Modal} from 'semantic-ui-react';

class TreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      treeId: props.tree.treeId,
      height: props.tree.height,
      diameter: props.tree.diameter,
      datePlanted: props.tree.datePlanted,
      land: props.tree.land,
      status: props.tree.status,
      ownership: props.tree.ownership,
      species: props.tree.species,
      location: props.tree.location,
      municipality: props.tree.municipality,
      reports: props.tree.reports,
      update: false,
      showReports: false
    }
  }

  onMakeUpdate = () => {
    this.setState({update: !this.state.update})
  }

  onShowReports = () => {
    this.setState({showReports: !this.state.showReports})
  }

  onUpdateTree = () => {
    this.setState({update: !this.state.update})
  }

  render() {
    return (
      <Modal
        open
        size='small'
        dimmer='blurring'
      >
        <Modal.Content>
          <Modal.Description>
            <Header as='h1' icon textAlign='center'>
              <Icon name='tree' circular/>
              <Header.Content>Tree</Header.Content>
            </Header>
            <Grid textAlign='center'>
              <Grid.Row columns={2}>
                <Grid.Column>
                  <Header as='h3' content='Tree ID'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Date Planted'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row columns={2}>
                <Grid.Column>
                  {this.state.treeId}
                </Grid.Column>
                <Grid.Column>
                  {this.state.datePlanted}
                </Grid.Column>
              </Grid.Row>

              <Grid.Row columns={3}>
                <Grid.Column>
                  <Header as='h3' content='Species'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Height (cm)'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Diameter (cm)'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row columns={3}>
                <Grid.Column>
                  {this.state.species.name}
                </Grid.Column>
                <Grid.Column>
                  {this.state.height}
                </Grid.Column>
                <Grid.Column>
                  {this.state.diameter}
                </Grid.Column>
              </Grid.Row>

              <Grid.Row columns={3}>
                <Grid.Column>
                  <Header as='h3' content='Status'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Ownership'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Land'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row columns={3}>
                <Grid.Column>
                  {this.state.status}
                </Grid.Column>
                <Grid.Column>
                  {this.state.ownership}
                </Grid.Column>
                <Grid.Column>
                  {this.state.land}
                </Grid.Column>
              </Grid.Row>

              <Grid.Row columns={3}>
                <Grid.Column>
                  <Header as='h3' content='Municipality'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Latitude'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Longitude'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row columns={3}>
                <Grid.Column>
                  {this.state.species.name}
                </Grid.Column>
                <Grid.Column>
                  {this.state.location.latitude}
                </Grid.Column>
                <Grid.Column>
                  {this.state.location.longitude}
                </Grid.Column>
              </Grid.Row>
            </Grid>

            <Header as='h3' textAlign='center'>
              <Header.Content>
                <Icon name='wpforms' onClick={this.onShowReports}/>Reports
              </Header.Content>
            </Header>
            {this.state.showReports ? (
              <div>
                <Grid textAlign='center' columns={3}>
                  <Grid.Column>
                    <Header as='h4' content='Report ID'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='User'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Date Modified'/>
                  </Grid.Column>
                </Grid>
                <Divider/>
                <Grid textAlign='center' columns={3}>
                  {this.state.reports.map(({reportId, reportUser, reportDate}) => {
                    return (
                      <Grid.Row key={reportId}>
                        <Grid.Column>
                          {reportId}
                        </Grid.Column>
                        <Grid.Column>
                          {reportUser}
                        </Grid.Column>
                        <Grid.Column>
                          {reportDate}
                        </Grid.Column>
                      </Grid.Row>
                    );
                  })}
                </Grid>
              </div>
            ) : null}

            {this.state.update ? (
              'map'
            ) : null}
            <Divider hidden/>
            <Grid centered>
              <Grid.Row>
                {this.state.update ? (
                  <Button inverted color='green' size='small' onClick={this.onUpdateTree}>Save</Button>
                ) : (
                  <Button inverted color='blue' size='small' onClick={this.onMakeUpdate}>Edit</Button>
                )}
                <Button inverted color='red' size='small' onClick={e => this.props.onClose(e, null)}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default TreeModal;
