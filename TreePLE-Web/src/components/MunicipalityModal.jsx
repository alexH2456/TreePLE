import React, {PureComponent} from 'react';
import {Button, Divider, Header, Icon, Grid, Modal} from 'semantic-ui-react';

class MunicipalityModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      name: props.municipality.name,
      totalTrees: props.municipality.totalTrees,
      borders: props.municipality.borders,
      update: false,
      showBorders: true
    }
  }

  onMakeUpdate = () => {
    this.setState({update: !this.state.update})
  }

  onShowBorders = () => {
    this.setState({showBorders: !this.state.showBorders})
  }

  onUpdateMunicipality = () => {
    this.setState({update: !this.state.update})
  }

  render() {
    console.log(this.state);
    return (
      <Modal
        open
        size='small'
        dimmer='blurring'
      >
        <Modal.Content>
          <Modal.Description>
            <Header as='h1' icon textAlign='center'>
              <Icon name='map' circular/>
              <Header.Content>Municipality</Header.Content>
            </Header>
            <Grid textAlign='center' columns={2}>
              <Grid.Row>
                <Grid.Column>
                  <Header as='h3' content='Name'/>
                </Grid.Column>
                <Grid.Column>
                  <Header as='h3' content='Total Trees'/>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row>
                <Grid.Column>
                  {this.state.name}
                </Grid.Column>
                <Grid.Column>
                  {this.state.totalTrees}
                </Grid.Column>
              </Grid.Row>
            </Grid>

            <Header as='h3' textAlign='center'>
              <Header.Content>
                <Icon name='point' onClick={this.onShowBorders}/>Borders
              </Header.Content>
            </Header>
            {this.state.showBorders ? (
              <div>
                <Grid textAlign='center' columns={3}>
                  <Grid.Column>
                    <Header as='h4' content='Location ID'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Latitude'/>
                  </Grid.Column>
                  <Grid.Column>
                    <Header as='h4' content='Longitude'/>
                  </Grid.Column>
                </Grid>
                <Divider/>
                <Grid textAlign='center' columns={3}>
                  {this.state.borders.map(({id, lat, lng}) => {
                    return (
                      <Grid.Row key={id}>
                        <Grid.Column>
                          {id}
                        </Grid.Column>
                        <Grid.Column>
                          {lat}
                        </Grid.Column>
                        <Grid.Column>
                          {lng}
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
                  <Button inverted color='green' size='small' onClick={this.onUpdateMunicipality}>Save</Button>
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

export default MunicipalityModal;
