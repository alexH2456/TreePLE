import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {getUserForecasts, deleteForecast} from './Requests';

class MyForecastsModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: '',
      forecasts: [],
      error: ''
    };
  }

  componentWillMount() {
    const user = localStorage.getItem('username');

    getUserForecasts(user)
      .then(({data}) => {
        this.setState({
          user: user,
          forecasts: data
        });
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onAnalysisForecast = (forecastId) => {
  }

  onDeleteForecast = (fcId) => {
    const fcParams = {
      user: this.state.user,
      forecastId: fcId
    };

    deleteForecast(fcParams)
      .then(() => {
        let forecasts = this.state.forecasts.slice();

        forecasts.some(({forecastId}, idx) => {
          if (forecastId === fcId) {
            forecasts.splice(idx, 1);
            this.setState({forecasts: forecasts});
            return true;
          }
        });
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
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
                <Header.Content as='h4'>Forecast ID</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Forecast Date</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Biodiversity Index</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Stormwater Intercepted<br/>(L/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>CO2 Sequestrated<br/>(KG/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Energy Conserved<br/>(KWH/YR)</Header.Content>
              </Grid.Column>
              <Grid.Column>
                <Header.Content as='h4'>Actions</Header.Content>
              </Grid.Column>
            </Grid>

            <Divider/>

            {this.state.forecasts.length !== 0 ? (
              <Grid textAlign='center' columns={7}>
                {this.state.forecasts.map(({forecastId, fcDate, biodiversity, stormwater, co2Reduced, energyConserved}) => (
                  <Grid.Row key={forecastId}>
                    <Grid.Column>{forecastId}</Grid.Column>
                    <Grid.Column>{fcDate}</Grid.Column>
                    <Grid.Column>{biodiversity.toFixed(5)}</Grid.Column>
                    <Grid.Column>{stormwater.toFixed(2)}</Grid.Column>
                    <Grid.Column>{co2Reduced.toFixed(2)}</Grid.Column>
                    <Grid.Column>{energyConserved.toFixed(2)}</Grid.Column>
                    <Grid.Column>
                      <Button inverted circular size='mini' content='Analysis' color='blue' onClick={() => this.onAnalysisForecast(forecastId)}/>
                      <Button inverted circular size='mini' icon='delete' color='red' disabled={!this.state.user} onClick={() => this.onDeleteForecast(forecastId)}/>
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
                <Message.Header style={{textAlign: 'center'}}>Looks like you haven't created any forecasts yet!</Message.Header>
              </Message>
            )}

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.props.onForecast}>Create</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

MyForecastsModal.propTypes = {
  onForecast: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired
};

export default MyForecastsModal;
