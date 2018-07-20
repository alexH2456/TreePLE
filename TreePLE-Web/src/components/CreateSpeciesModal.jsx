import React, {PureComponent} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Form, Grid, Header, Icon, Message, Modal} from 'semantic-ui-react';
import {createSpecies} from './Requests';
import {getError} from './Utils';

class CreateSpeciesModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      name: '',
      species: '',
      genus: '',
      error: ''
    };
  }

  onCreateSpecies = () => {
    const speciesParams = {
      name: this.state.name,
      species: this.state.species,
      genus: this.state.genus
    };

    createSpecies(speciesParams)
      .then(() => {
        this.props.onClose();
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  onNameChange = (e, {value}) => this.setState({name: value});
  onSpeciesChange = (e, {value}) => this.setState({species: value});
  onGenusChange = (e, {value}) => this.setState({genus: value});

  render() {
    const errors = getError(this.state.error);

    return (
      <Modal open size='small' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='dna' circular/>
              <Header.Content>Create Species</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Group widths='equal'>
                <Form.Input fluid label='Species (Name)' placeholder='Name' error={errors.species} onChange={this.onNameChange}/>
              </Form.Group>
              <Form.Group widths='equal'>
                <Form.Input fluid label='Species (Scientific)' placeholder='Species' error={errors.species} onChange={this.onSpeciesChange}/>
                <Form.Input fluid label='Genus' placeholder='Genus' error={errors.species} onChange={this.onGenusChange}/>
              </Form.Group>
            </Form>

            {this.state.error ? (
              <Message error>
                <Message.Header style={{textAlign: 'center'}}>{this.state.error}</Message.Header>
              </Message>
            ) : null}

            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateSpecies}>Create</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

CreateSpeciesModal.propTypes = {
  onClose: PropTypes.func.isRequired
};

export default CreateSpeciesModal;
