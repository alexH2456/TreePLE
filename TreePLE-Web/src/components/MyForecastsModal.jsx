import React, {PureComponent} from 'react';
import {compose, withProps} from 'recompose';
import {Button, Divider, Header, Icon, Grid, Message, Modal} from 'semantic-ui-react';
import {getUserForecasts} from "./Requests";

class MyForecastsModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: '',
      forecasts: []
    };
  }

  componentWillMount() {
    const user = localStorage.getItem('username');

    getUserForecasts(user)
      .then(({data}) => {
        this.setState({
          user: localStorage.getItem('username'),
          forecasts: data
        });
      })
      .catch(error => {
        console.log(error);
      });
  }

  render() {
    return (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='wpforms' circular/>
              <Header.Content>My Forecasts</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Grid textAlign='center' verticalAlign='middle' columns={7}>
              <Grid.Column>
                <Header as='h4' content='Forecast ID'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='Planified Date'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='Biodiversity Index'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='Stormwater Intercepted'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='CO2 Sequestrated'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='Energy Conserved'/>
              </Grid.Column>
              <Grid.Column>
                <Header as='h4' content='Actions'/>
              </Grid.Column>
            </Grid>

            <Divider/>

            {this.state.forecasts.length !== 0 ? (
              <Grid textAlign='center' columns={7}>
                {this.state.forecasts.map(({forecastId, fcDate, stormwater, co2Reduced, biodiversity, energyConserved}) => {
                  return (
                    <Grid.Row key={forecastId}>
                      <Grid.Column>{forecastId}</Grid.Column>
                      <Grid.Column>{fcDate}</Grid.Column>
                      <Grid.Column>{stormwater}</Grid.Column>
                      <Grid.Column>{co2Reduced}</Grid.Column>
                      <Grid.Column>{biodiversity}</Grid.Column>
                      <Grid.Column>{energyConserved}</Grid.Column>
                      <Grid.Column>Buttons</Grid.Column>
                    </Grid.Row>
                  );
                })}
              </Grid>
            ) : (
              <Message info>
                <Message.Header style={{textAlign: 'center'}}>Looks like you haven't created any forecasts yet!</Message.Header>
              </Message>
            )}

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='red' size='small' onClick={e => this.props.onClose(e, null)}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

export default MyForecastsModal;
